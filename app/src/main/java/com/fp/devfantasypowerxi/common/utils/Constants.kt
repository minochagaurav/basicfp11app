package com.fp.devfantasypowerxi.common.utils

import androidx.databinding.library.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor

class Constants {
    companion object {
        val HTTPLogLevel =
            if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.BODY
        const val SHARED_PREFERENCES_IS_LOGGED_IN = "is_logged_in"
        const val SHARED_PREFERENCE_USER_ID = "user_id"
        const val SHARED_PREFERENCE_JWT_TOKEN = "jwt_token"
        const val SHARED_PREFERENCE_USER_TOKEN = "user_token"
        const val SHARED_PREFERENCE_USER_NAME = "user_name"
        const val SHARED_PREFERENCE_USER_EMAIL = "user_email"
        const val SHARED_PREFERENCE_USER_MOBILE = "user_mobile"
        const val SHARED_PREFERENCE_USER_REFER_CODE = "user_refer_code"
        const val SHARED_PREFERENCE_USER_TEAM_NAME = "user_team_name"
        const val SHARED_PREFERENCE_USER_PIC = "user_pic_url"
        const val SHARED_PREFERENCE_USER_MOBILE_VERIFY_STATUS = "user_mobile_verify_status"
        const val SHARED_PREFERENCE_USER_EMAIL_VERIFY_STATUS = "user_email_verify_status"
        const val SHARED_PREFERENCE_USER_PAN_VERIFY_STATUS = "user_pan_verify_status"
        const val SHARED_PREFERENCE_USER_BANK_VERIFY_STATUS = "user_bank_verify_status"
        private const val API_VERSION = "1.1"
        const val ACCEPT_HEADER ="application/vnd.md.api.v$API_VERSION+json"

        const val KEY_USER_BALANCE = "key_user_balance"
        const val KEY_USER_WINING_AMOUNT = "KEY_USER_WINING_AMOUNT"
        const val KEY_USER_BONUS_AMOUNT = "KEY_USER_BONUS_AMOUNT"
        const val CONTEST_ID = "CONTEST_ID"
        const val SHARED_SPORTS_LIST = "SHARED_SPORTS_LIST"
        const val SHARED_PREFERENCE_USER_FCM_TOKEN = "user_fcm_token"

        const val TAG_FANTASY_TYPE_CLASSIC = "Classic"
        const val TAG_FANTASY_TYPE_BATTING = "Batting"
        const val TAG_FANTASY_TYPE_BOWLING = "Bowling"
        const val TAG_FANTASY_TYPE_PREMIUM = "Premium"

        const val KEY_LIVE_MATCH = 1
        const val KEY_UPCOMING_MATCH = 2
        const val KEY_FINISHED_MATCH = 3
        const val TOTAL_CREATE_TEAM_COUNT = 11

        const val KEY_TEAM_NAME = "key_team_name"
        //That use for ic_switch_team data
        const val KEY_MATCH_KEY = "key_match_key"
        const val KEY_WINING_AMOUNT = "key_wining_amount"
        const val KEY_TEAM_VS = "key_team_vs"
        const val KEY_TEAM_FIRST_URL = "key_team_first_url"
        const val KEY_TEAM_SECOND_URL = "key_team_second_url"
        const val KEY_TEAM_ID = "key_team_id"
        const val KEY_CONTEST_KEY = "key_contest_key"
        const val KEY_IS_FOR_JOIN_CONTEST = "key_is_for_join_contest"
        const val KEY_CONTEST_DATA = "key_contest_data"
        const val KEY_CONTEST_FIRST_TIME_DATA = "KEY_CONTEST_FIRST_TIME_DATA"
        const val KEY_IMAGE_URI = "KEY_IMAGE_URI"
        const val KEY_WINING_PERCENT = "key_winning_percent"

        const val KEY_FANTASY_TYPE = "KEY_FANTASY_TYPE"
        const val FIRST_CREATE_TEAM = "FIRST_CREATE_TEAM"
        const val KEY_FANTASY_TYPE_LIST = "KEY_FANTASY_TYPE_LIST"
        const val SPORT_KEY = "sport_key"
        const val JOIN_ID = "join_id"

        const val KEY_STATUS_HEADER_TEXT = "key_status_header_text"
        const val KEY_STATUS_IS_TIMER_HEADER = "key_status_is_timer_text"
        const val KEY_IS_FOR_SWITCH_TEAM = "key_is_for_switch_team"
        const val KEY_STATUS_IS_FOR_CONTEST_DETAILS = "key_is_for_contest_details"
        const val KEY_ALL_CONTEST = "key_all_contest"

        const val TAG_C_TEXT = "Contest won't cancelled"
        //TAGS
        const val TAG_CRICKET = "cricket"
        const val TAG_FOOTBALL = "football"
        const val TAG_BASKETBALL = "basketball"
        const val SF_SPORT_KEY = "SF_SPORT_KEY"
        const val KEY_TEAM_COUNT = "key_team_count"

        //Player Role for cricket
        const val KEY_PLAYER_ROLE_BAT = "batsman"
        const val KEY_PLAYER_ROLE_ALL_R = "allrounder"
        const val KEY_PLAYER_ROLE_BOL = "bowler"
        const val KEY_PLAYER_ROLE_KEEP = "keeper"
        var SKIP_CREATETEAM_INSTRUCTION = "SKIP_CREATETEAM_INSTRUCTION"
        var SKIP_CREATECVC_INSTRUCTION = "SKIP_CREATECVC_INSTRUCTION"

        //Player Role for Football
        const val KEY_PLAYER_ROLE_DEF = "Defender"
        const val KEY_PLAYER_ROLE_ST = "Forward"
        const val KEY_PLAYER_ROLE_GK = "Goalkeeper"
        const val KEY_PLAYER_ROLE_MID = "Midfielder"


        var SHOW_RULE_POPUP_CLASSIC_CRICKET = "SHOW_RULE_POPUP_CLASSIC_CRICKET"
        var SHOW_RULE_POPUP_BATTING_CRICKET = "SHOW_RULE_POPUP_BATTING_CRICKET"
        var SHOW_RULE_POPUP_BOWLING_CRICKET = "SHOW_RULE_POPUP_BOWLING_CRICKET"
        var SHOW_RULE_POPUP_PREMIUM_CRICKET = "SHOW_RULE_POPUP_PREMIUM_CRICKET"
        var SHOW_IN_APP_IMAGE_POPUP = "SHOW_IN_APP_IMAGE_POPUP"


        const val KEY_TEAM_LIST_WK = "key_team_list_wk"
        const val KEY_TEAM_LIST_BAT = "key_team_list_bat"
        const val KEY_TEAM_LIST_AR = "key_team_list_ar"
        const val KEY_TEAM_LIST_BOWL = "key_team_list_bowl"
        const val KEY_TEAM_LIST_C = "KEY_TEAM_LIST_C"


        var WK = 1
        var BAT = 2
        var AR = 3
        var BOWLER = 4


    }

}