<<<<<<< HEAD:TroChoiAmNhac/app/src/main/java/com/wordpress/nguyenvannamdev/trochoiamnhac/data/User.java
package com.wordpress.nguyenvannamdev.trochoiamnhac.data;

/**
 * Created by NAM COI on 2/25/2017.
 */

public class User {
    private String user_name;
    private int high_score;
    private  int hint;

    public User(String user_name, int hint, int high_score) {
        this.user_name = user_name;
        this.hint = hint;
        this.high_score = high_score;
    }

    public User() {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getHigh_score() {
        return high_score;
    }

    public void setHigh_score(int high_score) {
        this.high_score = high_score;
    }

    public int getHint() {
        return hint;
    }

    public void setHint(int hint) {
        this.hint = hint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return getUser_name().equals(user.getUser_name());

    }

    @Override
    public int hashCode() {
        return getUser_name().hashCode();
    }
}
=======
package com.wordpress.nguyenvannamdev.trochoiamnhac.firebaseService;

/**
 * Created by NAM COI on 2/25/2017.
 */

public class User {
    private String user_name;
    private int high_score;
    private  int hint;

    public User(String user_name, int hint, int high_score) {
        this.user_name = user_name;
        this.hint = hint;
        this.high_score = high_score;
    }

    public User() {
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public int getHigh_score() {
        return high_score;
    }

    public void setHigh_score(int high_score) {
        this.high_score = high_score;
    }

    public int getHint() {
        return hint;
    }

    public void setHint(int hint) {
        this.hint = hint;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;

        User user = (User) o;

        return getUser_name().equals(user.getUser_name());

    }

    @Override
    public int hashCode() {
        return getUser_name().hashCode();
    }
}
>>>>>>> 2452063f4e321eafc7151f0c831bf7b426c62b1e:TroChoiAmNhac/app/src/main/java/com/wordpress/nguyenvannamdev/trochoiamnhac/firebaseService/User.java
