<template>
  <div id="payment" class="virnect-card">
    <div class="container">
      <div class="title">
        <el-breadcrumb separator="/">
          <el-breadcrumb-item>{{ $t('menu.account') }}</el-breadcrumb-item>
          <el-breadcrumb-item>{{ $t('menu.payment') }}</el-breadcrumb-item>
        </el-breadcrumb>
        <h2>{{ $t('menu.payment') }}</h2>
        <p>{{ $t('payment.desc') }}</p>
      </div>
      <el-row>
        <el-col class="container__left">
          <!-- 결제 예정 정보 -->
          <el-card class="will">
            <div slot="header">
              <h3>{{ $t('payment.will.title') }}</h3>
            </div>
            <dl>
              <dt>{{ $t('payment.will.price') }}</dt>
              <dd class="price">
                <span>{{ (200000).toLocaleString() }}</span>
                <span>{{ $t('payment.monetaryUnit') }}</span>
              </dd>
              <el-divider />
              <dt>{{ $t('payment.will.dueDate') }}</dt>
              <dd>{{ new Date() | dateFormat }}</dd>
              <el-divider />
              <dt>{{ $t('payment.will.way') }}</dt>
              <dd>
                <span>{{ '신용카드' }}</span>
                <span class="desc">
                  {{ $t('payment.will.autoPaymentEveryMonth') }}
                </span>
              </dd>
            </dl>
            <el-button type="simple" class="wide">
              {{ $t('common.more') }}
            </el-button>
          </el-card>
          <!-- 결제 수단 등록 & 변경 -->
          <el-card class="way">
            <dl>
              <dt>{{ $t('payment.way.change') }}</dt>
              <dd>
                <p>{{ $t('payment.way.changeDesc') }}</p>
                <el-button type="simple" class="wide">
                  {{ $t('common.change') }}
                </el-button>
              </dd>
              <el-divider />
              <dt>{{ $t('payment.way.autoPayment') }}</dt>
              <dd class="auto-payment">
                <span>{{ $t('payment.way.autoPaymentNow') }}</span>
                <el-button type="simple">
                  {{ $t('payment.way.autoPaymentCancel') }}
                </el-button>
              </dd>
            </dl>
          </el-card>
        </el-col>
        <!-- 결제 이력 -->
        <el-col class="container__right">
          <el-card class="el-card--table">
            <div slot="header">
              <h3>{{ $t('payment.log.title') }}</h3>
            </div>
            <el-table :data="paymentLogs" class="clickable">
              <column-default
                :label="$t('payment.log.column.no')"
                prop="no"
                sortable
              />
              <column-default
                :label="$t('payment.log.column.way')"
                prop="way"
                :width="120"
                sortable
              />
              <column-price
                :label="$t('payment.log.column.price')"
                prop="price"
                :width="140"
                sortable
              />
              <column-date
                :label="$t('payment.log.column.paidDate')"
                prop="paidDate"
                :width="100"
                sortable
              />
            </el-table>
            <el-row type="flex" justify="center">
              <el-pagination
                layout="prev, pager, next"
                :total="paymentLogsTotal"
              >
              </el-pagination>
            </el-row>
          </el-card>
        </el-col>
      </el-row>
    </div>
  </div>
</template>

<script>
import columnMixin from '@/mixins/columns'
import filterMixin from '@/mixins/filters'
import paymentService from '@/services/payment'

export default {
  mixins: [columnMixin, filterMixin],
  async asyncData() {
    const { list, total } = await paymentService.searchPaymentLogs()
    return {
      paymentLogs: list,
      paymentLogsTotal: total,
    }
  },
}
</script>

<style lang="scss">
#payment {
  .el-button.wide {
    width: 100%;
    height: 36px;
    font-size: 14px;
  }
  .will {
    dt {
      display: inline-block;
    }
    dd {
      float: right;
      text-align: right;
    }
    .price {
      display: block;
      float: none;
      margin-top: 8px;
      & > span:first-child {
        color: #0b5bd8;
        font-size: 24px;
        line-height: 28px;
      }
    }
    .desc {
      display: block;
      color: $font-color-desc;
      font-size: 12.6px;
    }
    .el-button {
      margin: 30px 0 10px;
    }
  }
  .way {
    dd {
      color: $font-color-desc;
      font-size: 12px;
    }
    p {
      margin: 8px 0 16px;
      line-height: 20px;
    }
    .auto-payment {
      margin: 14px 0 8px;
      .el-button {
        float: right;
        margin-top: -5px;
      }
    }
  }
}
</style>
