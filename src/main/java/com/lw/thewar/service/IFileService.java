package com.lw.thewar.service;

import com.lw.thewar.common.ServerResponse;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IFileService {
    public List<String> uploadPhotoalbum(List<MultipartFile> files, String path, Integer userId);

    public ServerResponse updatePhoto(MultipartFile file, String path, Integer userId) throws IOException;

    public ServerResponse uploadAlbum(MultipartFile file, String path, Integer userId,String hxUserName) throws IOException;
}
