package com.example.ftpapplication;

import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.ftpapplication.utils.LocalFileListView;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {


    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
        LocalFileListView mockedListView = mock(LocalFileListView.class);
        when(mockedListView.getDescription()).thenReturn("This is Dummy Description");
        System.out.println(mockedListView.getDescription());

    }
}