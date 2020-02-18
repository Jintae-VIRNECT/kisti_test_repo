package com.virnect.workspace.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;

/**
 * Project: base
 * DATE: 2020-01-07
 * AUTHOR: JohnMark (Chang Jeong Hyeon)
 * EMAIL: practice1356@gmail.com
 * DESCRIPTION:
 */
public class SampleDTO {
    @Setter
    @Getter
    public static class SampleLoginDTO {
        @NotNull
        private String name;
        private String someData;
    }
}
