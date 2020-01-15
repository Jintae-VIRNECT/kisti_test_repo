<template>
  <div id="PageNotFound">
    <h1>404</h1>
    <p>
      <span style="font-size:28px;font-weight:bold;">앗!</span> 요청하신 페이지를 찾을 수 없습니다.
    </p>
    <p>요청하신 주소를 다시한번 확인 부탁드립니다.</p>
    <p>
      <a href="https://virnectremote.com/">VIRNECT Remote로 가기</a>
    </p>

    <!--
    필요시 다국어 적용
	<p v-html="$t('not_found.not_found_page')"></p>
    <p>{{ $t('not_found.require_url') }}</p>
    <p><a href="https://virnectremote.com/">{{ $t('not_found.go_to_virnect') }}</a></p>
    -->
  </div>
</template>
<script>
export default { name: "NotFound" };
</script>
<style scoped>
#PageNotFound {
  width: 420px;
  margin: 120px auto 0;
  text-align: center;
}
h1 {
  display: inline-block;
  margin: 0 auto 5px;
  font-size: 60px;
  background: linear-gradient(to right, #0064ff 0%, #6700ff 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

p {
  margin: 0 0 5px;
  font-size: 18px;
  line-height: 1.4;
}

a {
  display: inline-block;
  margin-top: 45px;
  padding: 5px 16px 7px;
  border-radius: 4px;
  background: linear-gradient(to right, #0064ff 0%, #6700ff 100%);
  color: #fff;
  text-decoration: none;
  font-size: 16px;
}
</style>