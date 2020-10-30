package com.nile.networthcalculator;

import android.content.ContentResolver;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class BalanceSheetModelFactory implements ViewModelProvider.Factory {
    private ContentResolver contentResolver;

    public BalanceSheetModelFactory(ContentResolver contentResolver) {
        this.contentResolver = contentResolver;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new BalanceSheetModel(contentResolver);
    }
}