<template>
  <footer id="footerSection" class="footer-section dark">
    <h2>
      <img :src="`${virnectLogo}`" />
    </h2>
    <div class="feedback-section">
      <ul class="policy-wrap">
        <li>
          <a :href="$t(`footer.catalog`)" target="_blank">{{
            $t(`footer.brochureDownload`)
          }}</a>
        </li>
        <li>
          <a :href="$t(`footer.solutionLeflet`)" target="_blank">{{
            $t(`footer.downloadProductsLeaflet`)
          }}</a>
        </li>
      </ul>
      <address>
        <ul>
          <li v-for="(list, idx) of $t(`footer.companyList`)" :key="idx">
            <p v-html="list"></p>
          </li>
        </ul>
        <ul>
          <li v-for="(list, idx) of $t(`footer.numberList`)" :key="idx">
            <p v-html="list"></p>
          </li>
        </ul>
        <ul>
          <li>
            <p>
              {{ $t('footer.experienceCenter.title') }}
              <em>{{ $t(`footer.experienceCenter.location`) }}</em>
            </p>
          </li>
          <li class="align">
            <p>
              {{ $t('footer.researchCenter.title') }}
              <em>{{ $t(`footer.researchCenter.location`) }}</em>
            </p>
          </li>
        </ul>
        <p>©VIRNECT CO., LTD. All rights reserved.</p>
        <ul class="policy-section">
          <li>
            <p>
              <a :href="`${urls.www}/terms`" target="_blank">
                {{ $t(`footer.terms`) }}
              </a>
            </p>
          </li>
          <li>
            <p>
              <a :href="`${urls.www}/policy`" target="_blank">
                {{ $t(`footer.privacyPolicy`) }}
              </a>
            </p>
          </li>
          <li>
            <p>
              <a :href="`${urls.www}/payment`" target="_blank">
                {{ $t(`footer.paymentTerms`) }}
              </a>
            </p>
          </li>
        </ul>
      </address>
    </div>
    <div class="sns-wrap">
      <article id="subscribeBox">
        <a :href="`${urls.www}/media/pr`" target="_blank">
          {{ $t(`footer.subscribe`) }}
        </a>
      </article>
      <ul>
        <li v-for="(sns, idx) of $t('footer.snsList')" :key="idx">
          <a :href="sns.link" target="_blank" :class="`sns-${sns.title}`">{{
            sns.title
          }}</a>
        </li>
      </ul>
    </div>
    <div @click="top" id="topScroll">TOP</div>
  </footer>
</template>

<script>
import urls from '../../urls'

export default {
  props: {
    urls: {
      type: Object,
      default() {
        return urls
      },
    },
  },
  data() {
    return {
      virnectLogo: require('@virnect/ui-assets/images/products/platform/logo_white.svg'),
    }
  },
  methods: {
    top() {
      window.scrollTo({
        behavior: 'smooth',
        left: 0,
        top: 0,
      })
    },
  }
}
</script>

<style lang="scss">
@import '~@virnect/ui-assets/css/footer.css';
// @import 'node_modules/@virnect/ui-assets/css/footer.css';
</style>
