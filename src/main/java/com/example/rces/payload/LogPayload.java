package com.example.rces.payload;

import java.time.LocalDateTime;
import java.util.Map;

public record LogPayload(LocalDateTime date, String userName, Map<String, String> metadata) {
}
