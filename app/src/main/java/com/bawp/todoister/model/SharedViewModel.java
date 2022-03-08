package com.bawp.todoister.model;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SharedViewModel extends ViewModel {
    private final MutableLiveData selectedItem = new MutableLiveData<>();

    public void selectItem(Task task) {
        selectedItem.setValue(task);
    }

    public LiveData<Task> getSelectedItem(){
        return selectedItem;
    }
}
