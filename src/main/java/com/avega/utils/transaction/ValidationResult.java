package com.avega.utils.transaction;

import java.time.LocalDate;
import java.util.List;


import lombok.Builder;
import lombok.Data;


@Builder
@Data
public class ValidationResult {

	private boolean status;
	private List<String> errors;
	private LocalDate parsedDate;

}
