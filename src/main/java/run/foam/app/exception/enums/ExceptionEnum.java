package run.foam.app.exception.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExceptionEnum {

    INVALID_USERNAME_PASSWORD(400,"用户名或密码错误！"),
    INVALID_PASSWORD(400,"原密码错误！"),
    TAGS_NUM_ERROR(400,"一篇文章标签数量不能超过5个！"),
    INVALID_PASSWORD_TWICE(400,"两次输入的密码不一致！"),
    POST_STATE_ERROR(400,"删除失败，文章状态错误！"),
    INVALID_TABLE_ORDER(400,"表格排序时发生未知错误！"),
    PARENT_ID_ERROR(400,"分类的父类不能选择自己！"),
    INVALID_ALIYUN_OSS_MESSAGE_ERROR(400,"所填阿里云OSS信息错误，请在 系统设置 => 博客设置 =>文件上传设置 仔细检查填写配置是否正确！"),
    INVALID_TOKEN(400,"token错误！"),
    TOKEN_VERIFY_ERROR(401,"token失效，请重新登录！"),
    TOKEN_NO_EXIST(401,"请先登录！"),
    STORAGE_ALREADY_EXIST(500,"您所要创建的阿里云OSS存储空间已存在！"),
    POST_CATEGORY_EXIST(403,"您所创建的分类已存在！"),
    POST_TAG_EXIST(403,"您所创建的标签已存在！"),
    STORAGE_CREATE_FAIL(500,"创建阿里云OSS存储空间失败！"),
    STORAGE_FIND_FAIL(500,"查找阿里云OSS存储空间失败，请检查文件上传设置相关信息！"),
    COMMENT_DELETE_FAIL(500,"删除评论信息失败！"),
    COMMENT_FIND_FAIL(500,"评论信息查找失败，请检查LeanCould信息是否填写正确！"),
    FILE_UPLOAD_ERROR(500,"文件上传失败！请检查所写的路径格式等相关信息！"),
    UPDATE_ERROR(500,"修改文件发生错误！请刷新页面后重试！"),
    ;
    private int code;
    private String msg;
}
