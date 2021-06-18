package com.example.blogsystem.ui.fragment;

import androidx.annotation.NonNull;
import androidx.navigation.ActionOnlyNavDirections;
import androidx.navigation.NavDirections;
import com.example.blogsystem.R;

public class LoginFragmentDirections {
  private LoginFragmentDirections() {
  }

  @NonNull
  public static NavDirections actionLoginFragmentToRegisterFragment() {
    return new ActionOnlyNavDirections(R.id.action_LoginFragment_to_RegisterFragment);
  }
}
