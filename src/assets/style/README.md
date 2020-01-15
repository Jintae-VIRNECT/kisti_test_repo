## VIRNECT Remote 스타일 가이드
### 스펙 
 
스타일시트는 CSS 전처리기 사용을 전제로 작성되었으며, 
이 문서의 샘플코드는 [SCSS](https://sass-lang.com/)를 사용하여 작성되었습니다. 
최신 크롬 브라우저를 지원합니다. `문서작성일 기준: Chrome 73.0.3683.103(공식 빌드) (64비트)`
 
### 폴더구조

```
/src/assets/style/
|-- account/
|-- account.scss
|-- admin/
|-- admin.scss
|-- mobileApp/
|-- service/
|-- service.scss
|-- plugin/
    |-- vue-toasted.scss
    `-- material-icons.scss
|-- _vars.scss
|-- _mixin.scss
|-- _font.scss
|-- _reset.scss
|-- common.scss
`-- policy.scss

```

#### common.scss 
프로젝트 전체 페이지에서 사용되는 Stylesheet들을 불러오는 파일. 
항상 전역(최상위 컴포넌트)에 들어가게 됩니다. 
 
 
#### _font.scss 
프로젝트에서 사용되는 폰트를 정의한 파일. 

 
#### _mixin.scss 
믹스인 변수명 선언 규칙은 @[속성]\_[디바이스]_[식별값] 형태로 작성합니다. 
 
 
#### _reset.less 
브라우저 간 공통된 스타일을 위한 스타일시트 초기화 파일입니다. 

### plugin/
프로젝트에서 사용되는 외부 플러그인 스타일시트를 포함한 디렉토리.  
 
___

### 스타일시트 작성 규칙 
 
 
#### 네이밍 규칙 
 
스타일시트 파일명은 모두 kebab-case로 작성하며 대문자는 사용하지 않습니다. 
클래스 명은 [BEM 방법론](http://getbem.com/naming/)을 채택하였습니다.

 
> kebab-case란? :: 하이픈으로 단어를 연결하는 표기법 
 
``` 
예시) 
 
remoteCall.scss  (X) 
Remote-call.scss (X) 
remote-call.scss (O) 
 
.blockElementModifier { overflow: hidden; }  (X) 
.block--Element__Modifier { overflow: hidden; }  (X) 
.block--element__modifier { overflow: hidden; } (O) 

 
``` 
 
 
#### 속성 작성 규칙 
 
CSS 속성 선언방식은 indent방식으로 작성합니다. 
 
> indent 방식이란? :: 한 줄에 1가지 속성만 작성하며, 선택자 안의 속성들은 들여쓰기를 하는 방식. 
 
CSS 속성 선언 순서는 레이아웃, BOX, 배경, 폰트, 기타 순으로 작성합니다. 
 
|  구분  | 속성목록(오름차순) |
| :----: | :----------------- |
| 레이아웃 	| display, visibility, overflow, float, clear, position, top, right, bottom, left, z-index 	|
| BOX 	| width, height, margin, padding, border 	|
| 배경 	| background 	|
| 폰트 	| font,color, letter-spacing, text-align, text-decoration, text-indent, vertical-align, white-space 	|
| 기타 	| transition, transform 	|
위에 언급되지 않은 나머지 속성들로 폰트의 관련 속성 이후에 선언하며, 기타 속성 내의 선언 순서는 무관함 
 
> vendor 속성과 브라우저 hack 속성은 관련 속성 바로 다음에 선언합니다.
> webpack 환경에서는 vendor와 hack을 사용하지 않습니다.

#### 해상도 대응

Desktop 최대 넓이: 1920px\
Desktop 최소 넓이: 1200px\
Desktop 여백: 25px
 
(1199px~1025px 영역은 가로 스크롤 존재. (1200px 기준) )
 
Pad 최대 넓이: 1024px;\
Pad 최소 넓이: 768px;
 
Mobile 최대 넓이: 767px;\
Mobile 최소 넓이: 320px;\
Mobile 여백: 20px;