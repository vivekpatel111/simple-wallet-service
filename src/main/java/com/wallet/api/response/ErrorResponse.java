package com.wallet.api.response;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter(AccessLevel.PUBLIC)
@Setter(AccessLevel.PRIVATE)
@ToString
public class ErrorResponse extends ApiResponse
{
    private String code;

    public ErrorResponse(String message, String code)
    {
        super(message);
        this.code = code;
    }
}
