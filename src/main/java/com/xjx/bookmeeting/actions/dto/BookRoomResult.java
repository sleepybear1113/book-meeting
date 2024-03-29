package com.xjx.bookmeeting.actions.dto;

import com.xjx.bookmeeting.exception.FrontException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/**
 * there is introduction
 *
 * @author XJX
 * @date 2021/9/14 15:53
 */
@EqualsAndHashCode(callSuper = true)
@Data
@ToString(callSuper = true)
public class BookRoomResult extends MeetingResponse<String> {
    @Serial
    private static final long serialVersionUID = 1171595966979555115L;
    public static final String SUCCESS_STRING = "true";

    private Long meetingId;
    private Integer isConflict;

    public static boolean isBookSuccess(BookRoomResult bookRoomResult) {
        if (bookRoomResult == null) {
            throw new FrontException("预定结果为 null");
        }
        if (SUCCESS_STRING.equals(bookRoomResult.getResultType())) {
            return true;
        }

        throw new FrontException(bookRoomResult.toString());
    }
}
