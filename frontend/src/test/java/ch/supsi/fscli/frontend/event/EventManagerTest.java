package ch.supsi.fscli.frontend.event;

import ch.supsi.fscli.frontend.listener.Listener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventManagerTest {
    private EventManager<Event> eventManager;

    @Mock
    private Listener<Event> listenerA;
    @Mock
    private Listener<Event> listenerB;
    @Mock
    private Event mockEvent;

    @BeforeEach
    void setUp() {
        eventManager = new EventManager<>();
    }

    @Test
    void testNotifyListeners() {
        eventManager.addListener(listenerA);
        eventManager.addListener(listenerB);
        eventManager.notify(mockEvent);
        verify(listenerA, times(1)).update(mockEvent);
        verify(listenerB, times(1)).update(mockEvent);
    }

    @Test
    void testRemoveListener() {
        eventManager.addListener(listenerA);
        eventManager.removeListener(listenerA);
        eventManager.notify(mockEvent);
        verify(listenerA, never()).update(any());
    }

    @Test
    void testNoDuplicateListeners() {
        eventManager.addListener(listenerA);
        eventManager.addListener(listenerA);
        eventManager.notify(mockEvent);
        verify(listenerA, times(1)).update(mockEvent);
    }

    @Test
    void testNullSafety() {
        eventManager.addListener(null);
        eventManager.addListener(listenerA);
        eventManager.notify(null);
        verify(listenerA, never()).update(any());
    }

    @Test
    void testExceptionIsolation() {
        doThrow(new RuntimeException("Simulated Failure")).when(listenerA).update(mockEvent);
        eventManager.addListener(listenerA);
        eventManager.addListener(listenerB);
        eventManager.notify(mockEvent);
        verify(listenerA).update(mockEvent);
        verify(listenerB, times(1)).update(mockEvent);
    }

    @Test
    void testReentrancy() {
        Listener<Event> selfDestructingListener = new Listener<>() {
            @Override
            public void update(Event event) {
                eventManager.removeListener(this);
            }
        };
        eventManager.addListener(selfDestructingListener);
        eventManager.addListener(listenerB);
        eventManager.notify(mockEvent);
        verify(listenerB, times(1)).update(mockEvent);
        eventManager.notify(mockEvent);
        verify(listenerB, times(2)).update(mockEvent);
    }
}