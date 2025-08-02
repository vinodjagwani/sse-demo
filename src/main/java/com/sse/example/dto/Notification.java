/**
 * Author: Vinod Jagwani
 */
package com.sse.example.dto;

public record Notification(String id,
                           String userId,
                           String message,
                           String timestamp,
                           boolean read) {

}
