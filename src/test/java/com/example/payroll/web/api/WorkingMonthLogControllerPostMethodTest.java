package com.example.payroll.web.api;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.stream.Stream;
import com.example.payroll.database.repository.WorkingMonthLogRepository;
import com.example.payroll.web.api.model.WorkingMonthLogResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.MediaType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class WorkingMonthLogControllerPostMethodTest {
  @Autowired
  private WebApplicationContext webApplicationContext;
  @Autowired
  private WorkingMonthLogRepository workingMonthLogRepository;
  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  static Stream<Arguments> postRequestArgumentsWithExpectedResult() {
    return Stream.of(
        Arguments.of("400 - 'Bad Request' because year validation fails",
            "/api/v1/payroll/employees/4003/working-month-log/1969/11",
            WorkingMonthLogResource.builder()
                .employeeId(4003L)
                .year(1969)
                .month(11)
                .workingHours(45)
                .build(),
            new ResultMatcher[]{
                status().isBadRequest(),
                content().string(containsString("must be greater than or equal to 1970"))}
        ),
        Arguments.of("400 - 'Bad Request' because month validation fails",
            "/api/v1/payroll/employees/4003/working-month-log/1970/12",
            WorkingMonthLogResource.builder()
                .employeeId(4003L)
                .year(1970)
                .month(12)
                .workingHours(35)
                .build(),
            new ResultMatcher[]{
                status().isBadRequest(),
                content().string(containsString("must be less than or equal to 11"))}
        ),
        Arguments.of("404 - 'Not Found' because employee not found",
            "/api/v1/payroll/employees/55/working-month-log/1970/8",
            WorkingMonthLogResource.builder()
                .employeeId(55L)
                .year(1970)
                .month(8)
                .workingHours(40)
                .build(),
            new ResultMatcher[]{
                status().isNotFound(),
                content().string(containsString("Could not find employee [id: 55]"))}
        ),
        Arguments.of("201 - 'Created' because request is fine",
            "/api/v1/payroll/employees/4003/working-month-log/2025/0",
            WorkingMonthLogResource.builder()
                .employeeId(4003L)
                .year(2025)
                .month(0)
                .workingHours(27)
                .build(),
            new ResultMatcher[]{
                status().isCreated(),
                header().exists("location"),
                content().contentType("application/hal+json"),
                jsonPath("$.employeeId").value(4003),
                jsonPath("$.year").value(2025),
                jsonPath("$.month").value(0),
                jsonPath("$.workingHours").value(27),
            }),
        Arguments.of("409 - 'Conflict' because working log already exists for employee with month and year",
            "/api/v1/payroll/employees/4003/working-month-log/1992/0",
            WorkingMonthLogResource.builder()
                .employeeId(4003L)
                .year(1992)
                .month(0)
                .workingHours(45)
                .build(),
            new ResultMatcher[]{status().isConflict()}
        ),
        Arguments.of("405 - 'Method Not Allowed' because month is missing in URI",
            "/api/v1/payroll/employees/4002/working-month-log/1970",
            WorkingMonthLogResource.builder().build(),
            new ResultMatcher[]{status().isMethodNotAllowed()}
        )
    );
  }

  @BeforeEach
  void setup() throws Exception {
    this.mockMvc = MockMvcBuilders.webAppContextSetup(this.webApplicationContext).build();
    this.objectMapper = new ObjectMapper();
    workingMonthLogRepository.deleteAll(); // remove all working month logs
    // create existing working month log
    this.mockMvc.perform(post("/api/v1/payroll/employees/{id}/working-month-log/{year}/{month}", 4003, 1992, 0)
        .contentType(MediaType.APPLICATION_JSON.toString())
        .content(objectMapper.writeValueAsString(WorkingMonthLogResource.builder()
            .employeeId(4003L)
            .year(1992)
            .month(0)
            .workingHours(43)
            .build())));
  }

  @ParameterizedTest(name = "{index}: {0}")
  @MethodSource("postRequestArgumentsWithExpectedResult")
  void GIVEN_post_request_is_performed_with_arguments_THEN_expected_result_matchers_are_matching(@SuppressWarnings("unused") String description, String uri, WorkingMonthLogResource resource, ResultMatcher[] expectedResultMatchers) throws Exception {
    // Arrange & Act & Assert
    this.mockMvc.perform(post(uri)
            .contentType(MediaType.APPLICATION_JSON.toString())
            .content(objectMapper.writeValueAsString(resource)))
        .andExpectAll(expectedResultMatchers);
  }
}
