/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Interface is used to define the onEmailCheck(boolean isAvailable) method, used to
 * decide if an email address is already used by another user.
 */


package com.example.lifesworkiguess;

public interface OnEmailCheckListener {
    void onEmailCheck(boolean isAvailable);
}
