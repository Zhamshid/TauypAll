package kz.app.taýypall.common;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DatabaseReference;

import kz.app.taýypall.R;


public class BaseFragment extends Fragment {
    private DatabaseReference ref;
    protected ProgressDialog progressDialog;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage(("Please wait"));
        progressDialog.setCancelable(false);

    }

    public void hideLoading() {
//        progressDialog.dismiss();

        try {
            if (progressDialog.isShowing())
                progressDialog.dismiss();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void showLoading() {
        if (progressDialog != null && !progressDialog.isShowing()) progressDialog.show();
    }

    public void openFragment(Fragment fragment){

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_layout,fragment)
                .commit();
    }

    public DatabaseReference getRef() {
        return ref;
    }

    public void showMessage(String s){
        Toast.makeText(getContext(), s ,Toast.LENGTH_SHORT).show();
    }




    public void openActivity(Class className) {

        Intent intent = new Intent(getContext(), className);
        startActivity(intent);
        getActivity();
        startActivity(intent);

    }


}
