package me.echeung.moemoekyun.viewmodels;

import android.content.Context;
import android.databinding.Bindable;

import com.android.databinding.library.baseAdapters.BR;

import me.echeung.moemoekyun.viewmodels.base.BaseViewModel;

public class UserViewModel extends BaseViewModel {

    private String userName;
    private int userRequests;
    private int queueSize;
    private int queuePosition;

    public UserViewModel(Context context) {
        super(context);
    }

    public void reset() {
        setUserName(null);
        setUserRequests(0);
        setQueueSize(0);
        setQueuePosition(0);
    }

    @Bindable
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
        notifyPropertyChanged(BR.userName);
    }

    @Bindable
    public int getUserRequests() {
        return userRequests;
    }

    public void setUserRequests(int userRequests) {
        this.userRequests = userRequests;
        notifyPropertyChanged(BR.userRequests);
    }

    @Bindable
    public int getQueueSize() {
        return queueSize;
    }

    public void setQueueSize(int queueSize) {
        this.queueSize = queueSize;
        notifyPropertyChanged(BR.queueSize);
    }

    @Bindable
    public int getQueuePosition() {
        return queuePosition;
    }

    public void setQueuePosition(int queuePosition) {
        this.queuePosition = queuePosition;
        notifyPropertyChanged(BR.queuePosition);
    }
}