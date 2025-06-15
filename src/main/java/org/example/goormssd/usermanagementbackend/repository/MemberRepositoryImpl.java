package org.example.goormssd.usermanagementbackend.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.core.types.dsl.BooleanExpression;
import lombok.RequiredArgsConstructor;
import org.example.goormssd.usermanagementbackend.domain.Member;
import org.example.goormssd.usermanagementbackend.domain.QMember;
import org.example.goormssd.usermanagementbackend.dto.member.request.MemberSearchConditionDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private static final QMember member = QMember.member;

    @Override
    public Page<Member> searchMembers(MemberSearchConditionDto condition, Pageable pageable) {
        List<Member> content = queryFactory
                .selectFrom(member)
                .where(
                        emailContains(condition.getEmail()),
                        usernameContains(condition.getUsername()),
                        emailVerifiedEq(condition.getEmailVerified()),
                        statusEq(condition.getStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(member.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = queryFactory
                .select(member.count())
                .from(member)
                .where(
                        emailContains(condition.getEmail()),
                        usernameContains(condition.getUsername()),
                        emailVerifiedEq(condition.getEmailVerified()),
                        statusEq(condition.getStatus())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression emailContains(String email) {
        return email != null && !email.isBlank()
                ? member.email.containsIgnoreCase(email)
                : null;
    }

    private BooleanExpression usernameContains(String username) {
        return username != null && !username.isBlank()
                ? member.username.containsIgnoreCase(username)
                : null;
    }

    private BooleanExpression emailVerifiedEq(Boolean emailVerified) {
        return emailVerified != null
                ? member.emailVerified.eq(emailVerified)
                : null;
    }

    private BooleanExpression statusEq(Member.Status status) {
        return status != null
                ? member.status.eq(status)
                : null;
    }
}

