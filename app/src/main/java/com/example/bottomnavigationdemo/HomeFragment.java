package com.example.bottomnavigationdemo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.time.Duration;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        homeViewModel =
                new ViewModelProvider( this ).get( HomeViewModel.class );
        View root = inflater.inflate( R.layout.fragment_home, container, false );
        final TextView textView = root.findViewById( R.id.tv_fh_test );
        homeViewModel.getText().observe( getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText( s );
            }
        } );

        final Button button = root.findViewById( R.id.btn_fh_test );
        button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textView.setText( homeViewModel.getDate() );
                Toast.makeText(requireContext(),"test",Toast.LENGTH_SHORT).show();
            }
        } );

        return root;
    }

}
