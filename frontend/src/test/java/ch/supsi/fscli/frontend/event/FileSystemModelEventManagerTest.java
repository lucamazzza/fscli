package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.listener.Listener;
import ch.supsi.fscli.frontend.util.AppError;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

class FileSystemModelEventManagerTest {
    private EventManager<FileSystemEvent> eventManager;
    private Listener<FileSystemEvent> mockListener;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager<>();

        mockListener = mock(Listener.class);

        eventManager.addListener(mockListener);
    }

    @Test
    void notify_sendsEventToRegisteredListener() {
        FileSystemEvent testEvent = new FileSystemEvent(AppError.SAVE_SUCCESS);

        eventManager.notify(testEvent);

        verify(mockListener, times(1)).update(testEvent);

        verifyNoMoreInteractions(mockListener);
    }

    @Test
    void notify_doesNotSendEventToRemovedListener() {
        FileSystemEvent testEvent = new FileSystemEvent(AppError.SAVE_SUCCESS);

        eventManager.removeListener(mockListener);

        eventManager.notify(testEvent);

        verify(mockListener, never()).update(any(FileSystemEvent.class));
    }

    @Test
    void notify_sendsEventToMultipleListeners() {
        Listener<FileSystemEvent> mockListener2 = mock(Listener.class);
        eventManager.addListener(mockListener2);

        FileSystemEvent testEvent = new FileSystemEvent(AppError.SAVE_SUCCESS);

        eventManager.notify(testEvent);

        verify(mockListener, times(1)).update(testEvent);
        verify(mockListener2, times(1)).update(testEvent);
    }

    @Test
    void notify_sendsCorrectEventData() {
        FileSystemEvent testEvent = new FileSystemEvent(AppError.SAVE_SUCCESS);
        ArgumentCaptor<FileSystemEvent> eventCaptor = ArgumentCaptor.forClass(FileSystemEvent.class);

        eventManager.notify(testEvent);
        verify(mockListener).update(eventCaptor.capture());

        FileSystemEvent capturedEvent = eventCaptor.getValue();
        assertNotNull(capturedEvent.error());
        assertEquals(testEvent, capturedEvent);
    }
}