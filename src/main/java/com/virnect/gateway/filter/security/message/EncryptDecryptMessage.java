package com.virnect.gateway.filter.security.message;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class EncryptDecryptMessage {
    private  String data;



    @Override
    public String toString() {
        return "EncryptDecryptMessage{" +
                "data='" + data + '\'' +
                '}';
    }
}
