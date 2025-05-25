package studitest.domain;

import studit.domain.Profile;

public class ProfileTest {
    public static void main(String[] args) {
        // 1. Profile 객체 생성
        Profile profile = new Profile();

        // 2. 관심 태그 추가
        profile.addInterest("머신러닝");
        profile.addInterest("딥러닝");
        profile.addInterest("데이터 분석");

        // 3. 중복 태그 추가 테스트
        profile.addInterest("머신러닝"); // 중복

        // 4. 관심 태그 확인
        System.out.println("✅ Profile 테스트 시작\n");
        System.out.println("현재 관심 태그: " + profile.getInterests());

        // 5. 특정 태그 포함 여부
        System.out.println("\n✔ 태그 포함 확인");
        System.out.println("딥러닝 포함? " + profile.hasInterest("딥러닝"));
        System.out.println("블록체인 포함? " + profile.hasInterest("블록체인"));

        // 6. 태그 제거
        profile.removeInterest("딥러닝");
        System.out.println("\n🧹 '딥러닝' 제거 후 태그: " + profile.getInterests());

        System.out.println("\n✅ Profile 테스트 완료");
    }
}
