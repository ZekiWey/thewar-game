package com.lw.thewar.service.impl;

import com.google.common.collect.Lists;
import com.lw.thewar.common.ServerResponse;
import com.lw.thewar.dao.PluginsMapper;
import com.lw.thewar.dao.UserAuthsMapper;
import com.lw.thewar.service.IFileService;
import com.lw.thewar.util.FTPUtil;
import net.coobird.thumbnailator.Thumbnailator;
import net.coobird.thumbnailator.Thumbnails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service("iFileService")
public class FileService implements IFileService {

    @Autowired
    private UserAuthsMapper userAuthsMapper;
    @Autowired
    private PluginsMapper pluginsMapper;

    private static Logger logger = LoggerFactory.getLogger(FileService.class);


    @Override
    public List<String> uploadPhotoalbum(List<MultipartFile> files, String path, Integer userId) {
        return null;
    }

    public ServerResponse uploadAlbum(MultipartFile file, String path, Integer userId,String hxUserName) throws IOException {

        String name = "";
        String albumStr = userAuthsMapper.selectAlbumByUserId(userId);
        List<String> albumList;
        if("null".equals(albumStr)){
            albumList = Lists.newArrayList();
            name = "1" + hxUserName;
        }else {
            albumList = Lists.newArrayList(albumStr.split(","));
            if(albumList.size() == 6){
                return ServerResponse.createByErrorMessage("已达上传最大上限");
            }
        }

        //生成文件名

        List<String> numlist = Lists.newArrayList();
        for (String s : albumList) {
            numlist.add(s.substring(0,1));
        }
        for (int i = 1; i <= 6; i++) {
            if(!numlist.contains(String.valueOf(i))){
                name = i + hxUserName;
                break;
            }
        }


        String fileName = file.getOriginalFilename();
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
        //todo 判断后缀名

        String upLoadFileName = name + "." + fileExtName;
        String thumbnailUpLoadFileName = name + "thumbnail"  + "." + fileExtName;

        logger.info("开始上传文件:文件名{}，路径{}，上传文件名{}", fileName, path, upLoadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, upLoadFileName);
        try {
            file.transferTo(targetFile);

            File thumbnailsFile = new File(path,thumbnailUpLoadFileName);
            Thumbnails.of(targetFile).scale(0.2f).toFile(thumbnailsFile);

            FTPUtil.upLoadFile(Lists.newArrayList(targetFile,thumbnailsFile));

            targetFile.delete();
            thumbnailsFile.delete();
        } catch (IOException e) {
            logger.error("上传文件 异常", e);
            return ServerResponse.createByErrorMessage("图片上传失败，请重试");
        }

        albumList.add(upLoadFileName);

        albumStr = "";
        for (String s : albumList) {
            albumStr += s + ",";
        }
        albumStr = albumStr.substring(0,albumStr.length()-1);


        int reulst = userAuthsMapper.updateAlbumById(albumStr,userId);

        if(reulst > 0){
            return ServerResponse.createBySuccessMsg("图片上传成功");
        }
        else {
            //todo 删除上传失败的文件
            return ServerResponse.createByErrorMessage("图片上传失败，请重试");
        }



    }

    public ServerResponse updatePhoto(MultipartFile file, String path, Integer userId) throws IOException {

        String hxUserName = pluginsMapper.selectHXUsernameByUserId(userId);

        if(hxUserName == null){
            return ServerResponse.createByErrorMessage("网络异常");
        }

        List<String> files_name = Lists.newArrayList();

        String fileName = file.getOriginalFilename();
        String fileExtName = fileName.substring(fileName.lastIndexOf(".") + 1);
        String name = hxUserName;
        String upLoadFileName = name + "." + fileExtName;

        String thumbnailUpLoadFileName = "thumbnail" + name  + "." + fileExtName;

        logger.info("开始上传文件:文件名{}，路径{}，上传文件名{}", fileName, path, upLoadFileName);

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, upLoadFileName);
        try {
            file.transferTo(targetFile);

            File thumbnailsFile = new File(path,thumbnailUpLoadFileName);
            Thumbnails.of(targetFile).scale(0.2f).toFile(thumbnailsFile);

            String oldPhoto = userAuthsMapper.selectUserPhoneById(userId);
            if(null == oldPhoto){
                return ServerResponse.createByErrorMessage("头像上传失败，请重试");
            }
            if(!oldPhoto.equals("default.jpg")){
                if(!FTPUtil.delefile(oldPhoto)){
                    FTPUtil.delefile("thumbnail" + oldPhoto);
                    return ServerResponse.createByErrorMessage("头像上传失败，请重试");
                }
            }

            FTPUtil.upLoadFile(Lists.newArrayList(targetFile,thumbnailsFile));


            targetFile.delete();
            thumbnailsFile.delete();
        } catch (IOException e) {
            logger.error("上传文件 异常", e);
            return ServerResponse.createByErrorMessage("头像上传失败，请重试");
        }



        int reulst = userAuthsMapper.updatePhotoById(targetFile.getName(),userId);

        if(reulst > 0){
            return ServerResponse.createBySuccessMsg("头像上传成功");
        }

        return ServerResponse.createByErrorMessage("头像上传失败，请重试");

    }

/*    //生成缩略图
    private boolean picZoom(String filePath){
        File file = new File(filePath);
    }*/
}
