package com.sparta.moit.domain.meeting.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sparta.moit.domain.meeting.entity.Meeting;
import com.sparta.moit.domain.meeting.entity.MeetingStatusEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j(topic = "GetMeetingDetailResponseDto")
@Getter
public class GetMeetingDetailResponseDto {

    private final Long meetingId;
    private final String meetingName;
    private final String creatorName;
    private final String creatorEmail;
    private final List<String> careerNameList;
    private final List<String> skillNameList;
    private final LocalDate meetingDate;

    //    @JsonFormat(pattern = "HH:mm")
    private final LocalDateTime meetingStartTime;
//    @JsonFormat(pattern = "HH:mm")
    private final LocalDateTime meetingEndTime;

    @JsonFormat(pattern = "HH:mm", timezone = "GMT+9")
    private final Date jsonFormatStartTime;

    @JsonFormat(pattern = "HH:mm", timezone = "GMT+9")
    private final LocalDateTime localJsonFormatStartTime;

    @JsonFormat(pattern = "HH:mm", timezone = "GMT+9")
    private final LocalDateTime utcFormatStartTime;

    private final String locationAddress;

    private final Short registeredCount;
    private final Short totalCount;
    private final Integer budget;
    private final String contents;
    private final Double locationLat;
    private final Double locationLng;
    private final boolean isJoin;
    private final MeetingStatusEnum status;
    private final boolean isBookmarked;

    @Builder
    public GetMeetingDetailResponseDto(Long meetingId, String meetingName, String creatorName, String creatorEmail, List<String> careerNameList, List<String> skillNameList, LocalDate meetingDate, LocalDateTime meetingStartTime, LocalDateTime meetingEndTime, Date jsonFormatStartTime, LocalDateTime localJsonFormatStartTime, LocalDateTime utcFormatStartTime, String locationAddress, Short registeredCount, Short totalCount, Integer budget, String contents, Double locationLat, Double locationLng, boolean isJoin, MeetingStatusEnum status, boolean isBookmarked) {
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.creatorName = creatorName;
        this.creatorEmail = creatorEmail;
        this.careerNameList = careerNameList;
        this.skillNameList = skillNameList;
        this.meetingDate = meetingDate;
        this.meetingStartTime = meetingStartTime;
        this.meetingEndTime = meetingEndTime;
        this.jsonFormatStartTime = jsonFormatStartTime;
        this.localJsonFormatStartTime = localJsonFormatStartTime;
        this.utcFormatStartTime = utcFormatStartTime;
        this.locationAddress = locationAddress;
        this.registeredCount = registeredCount;
        this.totalCount = totalCount;
        this.budget = budget;
        this.contents = contents;
        this.locationLat = locationLat;
        this.locationLng = locationLng;
        this.isJoin = isJoin;
        this.status = status;
        this.isBookmarked = isBookmarked;
    }

    public static GetMeetingDetailResponseDto fromEntity(Meeting meeting, boolean isJoin, boolean isBookmarked) {
        List<String> careerNameList = meeting.getCareerList().stream()
                .map(CareerResponseDto::getCareerName)
                .collect(Collectors.toList());

        List<String> skillNameList = meeting.getSkillList().stream()
                .map(SkillResponseDto::getSkillName)
                .collect(Collectors.toList());


        Date jsonFormatStartTime = Date.from(meeting.getMeetingStartTime().atZone(ZoneId.systemDefault()).toInstant());
        log.info("meeting.getMeetingStartTime() = " + meeting.getMeetingStartTime().toString());
        log.info("meeting.getMeetingEndTime() = " + meeting.getMeetingEndTime().toString());

        return GetMeetingDetailResponseDto.builder()
                .meetingId(meeting.getId())
                .meetingName(meeting.getMeetingName())
                .creatorName(meeting.getCreator().getUsername())
                .creatorEmail(meeting.getCreator().getEmail())
                .careerNameList(careerNameList)
                .skillNameList(skillNameList)
                .meetingDate(meeting.getMeetingDate())
                .meetingStartTime(meeting.getMeetingStartTime())
                .meetingEndTime(meeting.getMeetingEndTime())
                .jsonFormatStartTime(jsonFormatStartTime)
                .localJsonFormatStartTime(meeting.getMeetingStartTime())
                .utcFormatStartTime(meeting.getMeetingStartTime())
                .locationAddress(meeting.getLocationAddress())
                .registeredCount(meeting.getRegisteredCount())
                .totalCount(meeting.getTotalCount())
                .budget(meeting.getBudget())
                .contents(meeting.getContents())
                .locationLat(meeting.getLocationLat())
                .locationLng(meeting.getLocationLng())
                .isJoin(isJoin)
                .status(meeting.getStatus())
                .isBookmarked(isBookmarked)
                .build();
    }
}
