package ar.edu.palermo.devops.tp;

import ar.edu.palermo.devops.tp.controller.EventController;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TpApplicationTests extends AbstractContainer {

	@Autowired
	private EventController eventController;

	@Test
	void contextLoads() {
		Assertions.assertThat(eventController).isNotNull();
	}

	@Test
	void testContainerLoad() {
		Assertions.assertThat(POSTGRES_CONTAINER.isRunning()).isTrue();
	}
}
