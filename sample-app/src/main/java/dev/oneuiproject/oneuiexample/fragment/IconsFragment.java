package dev.oneuiproject.oneuiexample.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sec.sesl.tester.R;

public class IconsFragment extends BaseFragment {

    /*todo search & indexScroller*/

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        RecyclerView iconListView = view.findViewById(R.id.icon_recyclerview);

        iconListView.setLayoutManager(new LinearLayoutManager(getContext()));
        iconListView.setAdapter(new ImageAdapter());
        iconListView.setItemAnimator(null);
        iconListView.seslSetLastRoundedCorner(false);
        iconListView.seslSetFastScrollerEnabled(true);
    }

    @Override
    public int getLayoutResId() {
        return R.layout.sample3_fragment_icons;
    }

    @Override
    public int getIconResId() {
        return R.drawable.drawer_page_icon_icons;
    }

    @Override
    public CharSequence getTitle() {
        return "Icons";
    }


    public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ViewHolder> {

        @Override
        public int getItemCount() {
            return iconIds.length;
        }

        @Override
        public long getItemId(final int position) {
            return position;
        }

        @NonNull
        @Override
        public ImageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ImageAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.sample3_view_icon_listview_item, parent, false), viewType);
        }

        @Override
        public void onBindViewHolder(ImageAdapter.ViewHolder holder, final int position) {
            holder.imageView.setImageResource(iconIds[position]);
            holder.textView.setText(getResources().getResourceEntryName(iconIds[position]));
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imageView;
            TextView textView;

            ViewHolder(View itemView, int viewType) {
                super(itemView);
                imageView = itemView.findViewById(R.id.icon_list_item_icon);
                textView = itemView.findViewById(R.id.icon_list_item_text);
            }
        }
    }


    int[] iconIds = {
            R.drawable.ic_oui_ab_add,
            R.drawable.ic_oui_ab_app_info,
            R.drawable.ic_oui_ab_back,
            R.drawable.ic_oui_ab_call,
            R.drawable.ic_oui_ab_drawer,
            R.drawable.ic_oui_ab_favorites_off,
            R.drawable.ic_oui_ab_favorites_on,
            R.drawable.ic_oui_ab_more,
            R.drawable.ic_oui_ab_qr_code_scanner,
            R.drawable.ic_oui_ab_search,
            R.drawable.ic_oui_accessibility,
            R.drawable.ic_oui_accounts_backup,
            R.drawable.ic_oui_action_run_a_quick_command,
            R.drawable.ic_oui_action_video_enhancer,
            R.drawable.ic_oui_air_action,
            R.drawable.ic_oui_air_command,
            R.drawable.ic_oui_airplane_mode,
            R.drawable.ic_oui_alarm,
            R.drawable.ic_oui_alarm_dismissed,
            R.drawable.ic_oui_always_get_key_notifications,
            R.drawable.ic_oui_alwys_on_display,
            R.drawable.ic_oui_amplify_sound,
            R.drawable.ic_oui_animals,
            R.drawable.ic_oui_app_closed,
            R.drawable.ic_oui_app_open,
            R.drawable.ic_oui_app_opened,
            R.drawable.ic_oui_apps,
            R.drawable.ic_oui_attic,
            R.drawable.ic_oui_audio_noti,
            R.drawable.ic_oui_audio_noti_mute,
            R.drawable.ic_oui_audio_system,
            R.drawable.ic_oui_audio_system_mute,
            R.drawable.ic_oui_auto_sync,
            R.drawable.ic_oui_automation,
            R.drawable.ic_oui_battery,
            R.drawable.ic_oui_battery_icon,
            R.drawable.ic_oui_batterylevel,
            R.drawable.ic_oui_beans,
            R.drawable.ic_oui_beans_active_noise_canceling,
            R.drawable.ic_oui_beans_bixby_voice_wakeup,
            R.drawable.ic_oui_beans_equlizer,
            R.drawable.ic_oui_beans_gaming,
            R.drawable.ic_oui_beans_lock,
            R.drawable.ic_oui_beans_noti,
            R.drawable.ic_oui_beans_touchpad,
            R.drawable.ic_oui_bed,
            R.drawable.ic_oui_beep_once,
            R.drawable.ic_oui_beforebed,
            R.drawable.ic_oui_beforebed_easytutorials,
            R.drawable.ic_oui_benefit,
            R.drawable.ic_oui_berry,
            R.drawable.ic_oui_bixby,
            R.drawable.ic_oui_bixby_voice,
            R.drawable.ic_oui_bluetooth,
            R.drawable.ic_oui_bluetooth_device,
            R.drawable.ic_oui_bluetooth_device_connect,
            R.drawable.ic_oui_bluetooth_device_disconnect,
            R.drawable.ic_oui_brightness,
            R.drawable.ic_oui_bud_ambient_sound,
            R.drawable.ic_oui_bud_equlizer,
            R.drawable.ic_oui_bud_gaming,
            R.drawable.ic_oui_bud_lock,
            R.drawable.ic_oui_bud_noti,
            R.drawable.ic_oui_bud_touchpad,
            R.drawable.ic_oui_buds,
            R.drawable.ic_oui_buds_live,
            R.drawable.ic_oui_buds_pro,
            R.drawable.ic_oui_budspro,
            R.drawable.ic_oui_budspro__lock_touchpad,
            R.drawable.ic_oui_budspro_active_noise_canceling,
            R.drawable.ic_oui_budspro_bixby_voice_wake_up,
            R.drawable.ic_oui_budspro_detect_conversation,
            R.drawable.ic_oui_budspro_equalizer_buds_,
            R.drawable.ic_oui_budspro_gaming,
            R.drawable.ic_oui_budspro_notifications,
            R.drawable.ic_oui_budspro_touch_and_hold_touchpad,
            R.drawable.ic_oui_calendar_next_shedules,
            R.drawable.ic_oui_calendar_shedules_searched,
            R.drawable.ic_oui_call,
            R.drawable.ic_oui_call_text_other_device,
            R.drawable.ic_oui_camera_flash_noti,
            R.drawable.ic_oui_cat,
            R.drawable.ic_oui_category_accessibility,
            R.drawable.ic_oui_category_accounts_backup,
            R.drawable.ic_oui_category_advanced_options,
            R.drawable.ic_oui_category_apps,
            R.drawable.ic_oui_category_battery,
            R.drawable.ic_oui_category_beans,
            R.drawable.ic_oui_category_bixby_voice,
            R.drawable.ic_oui_category_budspro,
            R.drawable.ic_oui_category_connections,
            R.drawable.ic_oui_category_display,
            R.drawable.ic_oui_category_effect,
            R.drawable.ic_oui_category_functions,
            R.drawable.ic_oui_category_galaxy_bud_plus,
            R.drawable.ic_oui_category_galaxy_watch,
            R.drawable.ic_oui_category_location,
            R.drawable.ic_oui_category_lockscreen,
            R.drawable.ic_oui_category_music,
            R.drawable.ic_oui_category_my_routines,
            R.drawable.ic_oui_category_notifications,
            R.drawable.ic_oui_category_security,
            R.drawable.ic_oui_category_smartthings,
            R.drawable.ic_oui_category_sounds,
            R.drawable.ic_oui_charging,
            R.drawable.ic_oui_color_adjustment,
            R.drawable.ic_oui_color_inversion,
            R.drawable.ic_oui_confirm_before_next_action,
            R.drawable.ic_oui_connection,
            R.drawable.ic_oui_connections,
            R.drawable.ic_oui_context,
            R.drawable.ic_oui_control_device,
            R.drawable.ic_oui_cycling,
            R.drawable.ic_oui_dark,
            R.drawable.ic_oui_dark_mode,
            R.drawable.ic_oui_decline,
            R.drawable.ic_oui_delete,
            R.drawable.ic_oui_device,
            R.drawable.ic_oui_dialing_keyboard,
            R.drawable.ic_oui_digital_wellbeing,
            R.drawable.ic_oui_display,
            R.drawable.ic_oui_display_icon,
            R.drawable.ic_oui_disturb,
            R.drawable.ic_oui_do_not_disturb,
            R.drawable.ic_oui_dog,
            R.drawable.ic_oui_dolby_atmos,
            R.drawable.ic_oui_driving,
            R.drawable.ic_oui_driving_condition,
            R.drawable.ic_oui_driving_easytutorials,
            R.drawable.ic_oui_during_call,
            R.drawable.ic_oui_during_call_condition,
            R.drawable.ic_oui_during_call_profile,
            R.drawable.ic_oui_edge_light,
            R.drawable.ic_oui_edge_panels,
            R.drawable.ic_oui_enhanced_processing,
            R.drawable.ic_oui_equalizer,
            R.drawable.ic_oui_error,
            R.drawable.ic_oui_event,
            R.drawable.ic_oui_eye_comfort_shield,
            R.drawable.ic_oui_favorit_off,
            R.drawable.ic_oui_favorit_on,
            R.drawable.ic_oui_flashlight,
            R.drawable.ic_oui_flex_mode,
            R.drawable.ic_oui_folding_status,
            R.drawable.ic_oui_folding_status_top,
            R.drawable.ic_oui_font_size,
            R.drawable.ic_oui_functions,
            R.drawable.ic_oui_galaxy_bud_plus,
            R.drawable.ic_oui_galaxy_watch,
            R.drawable.ic_oui_gallery_photo_search,
            R.drawable.ic_oui_game,
            R.drawable.ic_oui_game_profile,
            R.drawable.ic_oui_going_out,
            R.drawable.ic_oui_going_work,
            R.drawable.ic_oui_hart,
            R.drawable.ic_oui_heading_home,
            R.drawable.ic_oui_headphones,
            R.drawable.ic_oui_health,
            R.drawable.ic_oui_hear,
            R.drawable.ic_oui_hide_an_app,
            R.drawable.ic_oui_home,
            R.drawable.ic_oui_home_easytutorials,
            R.drawable.ic_oui_internet_website,
            R.drawable.ic_oui_keyboard_sound,
            R.drawable.ic_oui_keyboard_vibration,
            R.drawable.ic_oui_kids,
            R.drawable.ic_oui_labs,
            R.drawable.ic_oui_like,
            R.drawable.ic_oui_link_to_windows,
            R.drawable.ic_oui_live_caption,
            R.drawable.ic_oui_location,
            R.drawable.ic_oui_lockdown_the_phone,
            R.drawable.ic_oui_lockscreen,
            R.drawable.ic_oui_makeup,
            R.drawable.ic_oui_media_volume,
            R.drawable.ic_oui_meeting,
            R.drawable.ic_oui_message,
            R.drawable.ic_oui_message_all_read,
            R.drawable.ic_oui_message_search,
            R.drawable.ic_oui_message_send,
            R.drawable.ic_oui_mobile_data,
            R.drawable.ic_oui_mobile_hotspot,
            R.drawable.ic_oui_mono_audio,
            R.drawable.ic_oui_morning,
            R.drawable.ic_oui_morning_easytutorials,
            R.drawable.ic_oui_multi_window_tray,
            R.drawable.ic_oui_music,
            R.drawable.ic_oui_music_icon,
            R.drawable.ic_oui_music_share,
            R.drawable.ic_oui_mute,
            R.drawable.ic_oui_mute_all_sound,
            R.drawable.ic_oui_my_routines,
            R.drawable.ic_oui_myrouitnes_card,
            R.drawable.ic_oui_nature,
            R.drawable.ic_oui_navigate,
            R.drawable.ic_oui_navigationbar,
            R.drawable.ic_oui_nfc,
            R.drawable.ic_oui_nfc_tagged,
            R.drawable.ic_oui_night_life,
            R.drawable.ic_oui_notification_received_with_keyword,
            R.drawable.ic_oui_notification_with_keyword,
            R.drawable.ic_oui_notifications,
            R.drawable.ic_oui_open_app,
            R.drawable.ic_oui_open_split_view,
            R.drawable.ic_oui_parking,
            R.drawable.ic_oui_period_time,
            R.drawable.ic_oui_pets,
            R.drawable.ic_oui_phone_call_someone,
            R.drawable.ic_oui_place,
            R.drawable.ic_oui_planner,
            R.drawable.ic_oui_play_music,
            R.drawable.ic_oui_playlist,
            R.drawable.ic_oui_prompt_from_menu,
            R.drawable.ic_oui_puzzle,
            R.drawable.ic_oui_quick_command,
            R.drawable.ic_oui_read,
            R.drawable.ic_oui_read_book,
            R.drawable.ic_oui_receiving_message_from_keywords,
            R.drawable.ic_oui_receiving_message_from_someone,
            R.drawable.ic_oui_receiving_message_including_some_keywords,
            R.drawable.ic_oui_refresh_rate,
            R.drawable.ic_oui_remove,
            R.drawable.ic_oui_restaurants,
            R.drawable.ic_oui_reverted,
            R.drawable.ic_oui_robot,
            R.drawable.ic_oui_rotate,
            R.drawable.ic_oui_routines,
            R.drawable.ic_oui_samsung_health,
            R.drawable.ic_oui_save_battery,
            R.drawable.ic_oui_school,
            R.drawable.ic_oui_screen_flash_noti,
            R.drawable.ic_oui_screen_off,
            R.drawable.ic_oui_screen_orientation,
            R.drawable.ic_oui_screen_resolution,
            R.drawable.ic_oui_screen_shortcuts,
            R.drawable.ic_oui_screen_timeout,
            R.drawable.ic_oui_screen_zoom,
            R.drawable.ic_oui_script,
            R.drawable.ic_oui_secure_wifi,
            R.drawable.ic_oui_security,
            R.drawable.ic_oui_separate,
            R.drawable.ic_oui_shopping,
            R.drawable.ic_oui_show_action_list,
            R.drawable.ic_oui_show_custom_notification,
            R.drawable.ic_oui_smart_popup,
            R.drawable.ic_oui_smartthings,
            R.drawable.ic_oui_smile_emoticon,
            R.drawable.ic_oui_sound,
            R.drawable.ic_oui_sound_2,
            R.drawable.ic_oui_sound_balance,
            R.drawable.ic_oui_sound_mute,
            R.drawable.ic_oui_sound_vibrate,
            R.drawable.ic_oui_soundmode,
            R.drawable.ic_oui_sounds,
            R.drawable.ic_oui_sports,
            R.drawable.ic_oui_spotify,
            R.drawable.ic_oui_star,
            R.drawable.ic_oui_start,
            R.drawable.ic_oui_start_a_stopwatch,
            R.drawable.ic_oui_streaming,
            R.drawable.ic_oui_time,
            R.drawable.ic_oui_time_profile,
            R.drawable.ic_oui_timer,
            R.drawable.ic_oui_tips,
            R.drawable.ic_oui_travel,
            R.drawable.ic_oui_traveling,
            R.drawable.ic_oui_turn_off_alarms,
            R.drawable.ic_oui_turn_on_alarms,
            R.drawable.ic_oui_unlock_phone,
            R.drawable.ic_oui_vibrate,
            R.drawable.ic_oui_video_enhancer,
            R.drawable.ic_oui_voice_wakeup,
            R.drawable.ic_oui_volume,
            R.drawable.ic_oui_vpn_connect,
            R.drawable.ic_oui_vpn_disconnect,
            R.drawable.ic_oui_wake_up_time,
            R.drawable.ic_oui_watch_theatermode,
            R.drawable.ic_oui_weather,
            R.drawable.ic_oui_wifi,
            R.drawable.ic_oui_wifi_2,
            R.drawable.ic_oui_wifi_network,
            R.drawable.ic_oui_wifi_strength_1,
            R.drawable.ic_oui_wifi_strength_2,
            R.drawable.ic_oui_wifi_strength_3,
            R.drawable.ic_oui_wifi_strength_4,
            R.drawable.ic_oui_work,
            R.drawable.ic_oui_workout
    };
}
