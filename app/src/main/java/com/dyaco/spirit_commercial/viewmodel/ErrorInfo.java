package com.dyaco.spirit_commercial.viewmodel;


import com.dyaco.spirit_commercial.support.intdef.GENERAL;

public class ErrorInfo {
    private @GENERAL.UartError int  uartErrorType;
    private String                   m_title;
    private String                   m_desc;

    public ErrorInfo(@GENERAL.UartError int uartErrorType, String m_title, String m_desc) {
        this.uartErrorType = uartErrorType;
        this.m_title = m_title;
        this.m_desc = m_desc;
    }

    public @GENERAL.UartError int getUartErrorType() {
        return uartErrorType;
    }

    public void setUartErrorType(@GENERAL.UartError int uartErrorType) {
        this.uartErrorType = uartErrorType;
    }

    public String getTitle() {
        return m_title;
    }

    public void setTitle(String m_title) {
        this.m_title = m_title;
    }

    public String getDesc() {
        return m_desc;
    }

    public void setDesc(String m_desc) {
        this.m_desc = m_desc;
    }
}
