<template>
  <el-dialog
    class="coupon-detail-modal"
    :title="$t('coupon.detailModal.title')"
    :visible.sync="visible"
    width="580px"
    :before-close="handleClose"
  >
    <dl>
      <dt>{{ $t('coupon.detailModal.couponName') }}</dt>
      <dd>{{ coupon.name }}</dd>
      <dt>{{ $t('coupon.detailModal.couponNo') }}</dt>
      <dd>{{ coupon.no }}</dd>
      <dt>{{ $t('coupon.detailModal.couponType') }}</dt>
      <dd>{{ coupon.typeName }}</dd>
      <dt>{{ $t('coupon.detailModal.registerDate') }}</dt>
      <dd>{{ coupon.registerDate }}</dd>
      <dt>{{ $t('coupon.detailModal.usedDate') }}</dt>
      <dd>{{ coupon.usedDate }}</dd>
      <dt>{{ $t('coupon.detailModal.expireDate') }}</dt>
      <dd>{{ coupon.expiredDate }}</dd>
      <dt>{{ $t('coupon.detailModal.target') }}</dt>
      <dd>{{ coupon.applyItemName }}</dd>
    </dl>
    <p>{{ $t('coupon.detailModal.desc') }}</p>
    <template slot="footer">
      <a :href="$url.pay">
        <el-button type="primary">
          {{ $t('common.payCenter') }}
        </el-button>
      </a>
    </template>
  </el-dialog>
</template>

<script>
import dialogMixin from '@/mixins/dialog'
import columnMixin from '@/mixins/columns'

export default {
  mixins: [dialogMixin, columnMixin],
  props: {
    coupon: Object,
  },
}
</script>

<style lang="scss">
#__nuxt .coupon-detail-modal {
  dd {
    margin-bottom: 10px;
  }
}
</style>
