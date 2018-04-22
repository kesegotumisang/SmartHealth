package bw.ub.cs.smarthealth;


public class MessageModel {

    private String timestamp, username, userId, postId, message, gplusId, profileImg;

    // Empty Constructor Required
    public MessageModel(){}

    public MessageModel(String timestamp, String username, String userId, String postId, String message, String gplusId, String profileImg) {
        this.timestamp = timestamp;
        this.username = username;
        this.userId = userId;
        this.postId = postId;
        this.message = message;
        this.gplusId = gplusId;
        this.profileImg = profileImg;
    }

    public String getProfileImg() {
        return profileImg;
    }

    public void setProfileImg(String profileImg) {
        this.profileImg = profileImg;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGplusId() {
        return gplusId;
    }

    public void setGplusId(String gplusId) {
        this.gplusId = gplusId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

