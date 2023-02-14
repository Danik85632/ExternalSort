package com.company;

import java.io.IOException;

public interface IOStringStack {
    public void close() throws IOException;

    public boolean empty();

    public String peek();

    public String pop() throws IOException;

}