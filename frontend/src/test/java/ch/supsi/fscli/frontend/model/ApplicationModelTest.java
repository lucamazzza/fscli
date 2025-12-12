package ch.supsi.fscli.frontend.model;

import ch.supsi.fscli.frontend.event.AboutEvent;
import ch.supsi.fscli.frontend.event.EventPublisher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ApplicationModelTest {

    @Mock
    private EventPublisher<AboutEvent> mockPublisher;

    @Test
    void testModelLoadsProvidedProperties() {
        Properties testProps = new Properties();
        testProps.setProperty("app.name", "TestApp");
        testProps.setProperty("app.version", "9.9.9");

        ApplicationModel model = new ApplicationModel(testProps);
        model.setAboutEventPublisher(mockPublisher);

        model.getAppInfo();

        ArgumentCaptor<AboutEvent> captor = ArgumentCaptor.forClass(AboutEvent.class);
        verify(mockPublisher).notify(captor.capture());

        assertEquals("TestApp", captor.getValue().appInfo().get("AppName"));
        assertEquals("9.9.9", captor.getValue().appInfo().get("Version"));
    }

    @Test
    void testDefaultsAreLoadedOnEmptyProperties() {
        ApplicationModel model = new ApplicationModel(new Properties());
        model.setAboutEventPublisher(mockPublisher);

        model.getAppInfo();

        ArgumentCaptor<AboutEvent> captor = ArgumentCaptor.forClass(AboutEvent.class);
        verify(mockPublisher).notify(captor.capture());

        assertEquals("FSCLI", captor.getValue().appInfo().get("AppName"));
    }
}