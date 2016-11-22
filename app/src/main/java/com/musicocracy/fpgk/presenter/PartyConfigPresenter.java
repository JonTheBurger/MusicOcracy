package com.musicocracy.fpgk.presenter;

import android.content.Context;

import com.musicocracy.fpgk.model.PartyConfigModel;
import com.musicocracy.fpgk.model.net.IpUtils;
import com.musicocracy.fpgk.view.PartyConfigView;

public class PartyConfigPresenter implements Presenter<PartyConfigView> {
    private final PartyConfigModel model;
    private PartyConfigView view;

    public PartyConfigPresenter(PartyConfigModel model) {
        this.model = model;
    }

    public void onCreate(Context context) {
        String address = IpUtils.getMyIpAddress(context);
        view.setPartyCode(IpUtils.ipAddressToBase36(address));
    }

    public void confirmSettings() {
        model.getSettings().setPartyCode(view.getPartyCode());
        model.getSettings().setPartyName(view.getPartyName());
        model.getSettings().setTokens(view.getTokenCount());
        long refillMillis = (view.getTokenRefillMinutes() * 60 + view.getTokenRefillSeconds()) * 1000;
        model.getSettings().setTokenRefillMillis(refillMillis);
    }

    @Override
    public void setView(PartyConfigView view) {
        this.view = view;
    }
}
