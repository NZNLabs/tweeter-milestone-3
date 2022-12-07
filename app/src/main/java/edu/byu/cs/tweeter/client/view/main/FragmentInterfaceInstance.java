package edu.byu.cs.tweeter.client.view.main;

import androidx.fragment.app.Fragment;

public class FragmentInterfaceInstance {
    // Static variable reference of single_instance
    // of type Singleton
    private static FragmentInterfaceInstance single_instance = null;

    // Declaring a variable of type String
    public Fragment fragmentInterfaceInstance;

    //    // Constructor
//    // Here we will be creating private constructor
//    // restricted to this class itself
//    private FragmentInterfaceInstance()
//    {
//        s = "Hello I am a string part of Singleton class";
//    }

    // Static method
    // Static method to create instance of Singleton class
    public static FragmentInterfaceInstance getInstance() {
        if (single_instance == null)
            single_instance = new FragmentInterfaceInstance();

        return single_instance;
    }
}
