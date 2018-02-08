package com.lw.thewar.pojo;

public class ChatGroup extends ChatGroupKey {
    private Long groupId;

    private String groupName;

    private String groupDesc;

    private String headImage;

    private Integer isPublic;

    public ChatGroup(Integer id, Integer ownerId, Long groupId, String groupName, String groupDesc, String headImage, Integer isPublic) {
        super(id, ownerId);
        this.groupId = groupId;
        this.groupName = groupName;
        this.groupDesc = groupDesc;
        this.headImage = headImage;
        this.isPublic = isPublic;
    }

    public ChatGroup() {
        super();
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName == null ? null : groupName.trim();
    }

    public String getGroupDesc() {
        return groupDesc;
    }

    public void setGroupDesc(String groupDesc) {
        this.groupDesc = groupDesc == null ? null : groupDesc.trim();
    }

    public String getHeadImage() {
        return headImage;
    }

    public void setHeadImage(String headImage) {
        this.headImage = headImage == null ? null : headImage.trim();
    }

    public Integer getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(Integer isPublic) {
        this.isPublic = isPublic;
    }
}