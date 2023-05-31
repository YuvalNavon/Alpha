/**
 * @author		Yuval Navon <yuvalnavon8@gmail.com>
 * @version 	1
 * @since		31/5/2023
 * This Interface is used to define the onUsernameCheck(boolean isAvailable) method, used to
 * decide if a username is already used by another user.
 */
package com.example.lifesworkiguess;

public interface OnUsernameCheckListener {
    void onUsernameCheck(boolean isAvailable);
}
