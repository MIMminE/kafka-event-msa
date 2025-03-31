package dev.nuts.spec;

import java.util.Date;

/**
 * 트위터 API 라이브러리에서 사용되는 Status 인터페이스를 간소화하여 사용하기 위한 인터페이스
 * 트위터 라이브러리에 대한 종속성을 가지지 않고 실습을 진행하기 위함
 */
public interface Status {

    Date getCreatedAt();

    Long getId();

    String getText();
}
