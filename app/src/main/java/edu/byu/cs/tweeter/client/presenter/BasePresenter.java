package edu.byu.cs.tweeter.client.presenter;


import edu.byu.cs.tweeter.client.utils.BaseViewInterface;

public class BasePresenter<VIEW extends BaseViewInterface>{
    public VIEW view;

    public BasePresenter(VIEW view) {
        this.view = view;
    }
}
