package com.sigma.auth.payload.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OutputMessage {

    private String from;
    private String text;
    private String time;

    public OutputMessage(String from, String text, String time) {
        this.from = from;
        this.text = text;
        this.time = time;
    }

}
