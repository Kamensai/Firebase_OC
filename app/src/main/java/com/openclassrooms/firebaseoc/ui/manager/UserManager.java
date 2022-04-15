package com.openclassrooms.firebaseoc.ui.manager;

import com.google.firebase.auth.FirebaseUser;
import com.openclassrooms.firebaseoc.ui.repository.UserRepository;

/**
 * Created by <Victor Khamvongsa> on <15/04/2022>
 */
public class UserManager {

    private static volatile UserManager instance;
    private  UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }
}
