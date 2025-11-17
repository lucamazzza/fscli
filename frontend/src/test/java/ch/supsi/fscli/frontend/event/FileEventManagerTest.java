package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.listener.Listener;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class FileEventManagerTest {
    private FileEventManager eventManager;
    private Listener<FileEvent> mockListener;

    @BeforeEach
    void setUp() {
        eventManager = new FileEventManager();

        mockListener = mock(Listener.class);

        eventManager.addListener(mockListener);
    }

    @Test
    void notify_sendsEventToRegisteredListener() {
        FileEvent testEvent = new FileEvent(EventError.SUCCESS,"test-file.txt",true);

        eventManager.notify(testEvent);

        verify(mockListener, times(1)).update(testEvent);

        verifyNoMoreInteractions(mockListener);
    }

    @Test
    void notify_doesNotSendEventToRemovedListener() {
        FileEvent testEvent = new FileEvent(EventError.SUCCESS,"test-file.txt",true);

        eventManager.removeListener(mockListener);

        eventManager.notify(testEvent);

        verify(mockListener, never()).update(any(FileEvent.class));
    }

    @Test
    void notify_sendsEventToMultipleListeners() {
        Listener<FileEvent> mockListener2 = mock(Listener.class);
        eventManager.addListener(mockListener2);

        FileEvent testEvent = new FileEvent(EventError.SUCCESS, "multi-listener-test.txt", true);

        eventManager.notify(testEvent);

        verify(mockListener, times(1)).update(testEvent);
        verify(mockListener2, times(1)).update(testEvent);
    }

    @Test
    void notify_sendsCorrectEventData() {
        FileEvent testEvent = new FileEvent(EventError.SUCCESS, "check-me.txt", true);
        ArgumentCaptor<FileEvent> eventCaptor = ArgumentCaptor.forClass(FileEvent.class);

        eventManager.notify(testEvent);
        verify(mockListener).update(eventCaptor.capture());

        FileEvent capturedEvent = eventCaptor.getValue();
        assertEquals("check-me.txt", capturedEvent.getMessage());
        assertEquals(testEvent, capturedEvent);
    }
}