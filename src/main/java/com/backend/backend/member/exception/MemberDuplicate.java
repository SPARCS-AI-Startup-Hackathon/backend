package com.backend.backend.member.exception;

import com.backend.backend.global.exception.GlobalException;

public class MemberDuplicate extends GlobalException {

    /**
     * 상태코드 -> 404
     */
        private static final String MESSAGE = "중복된 이메일입니다.";

        public MemberDuplicate() {
            super(MESSAGE);
        }
        @Override
        public int getStatusCode() {
            return 404;
    }
}
