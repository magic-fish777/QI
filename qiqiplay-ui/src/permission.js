import router from './router'
import store from './store'
import { Message } from 'element-ui'
import NProgress from 'nprogress'
import 'nprogress/nprogress.css'
import { getToken } from '@/utils/auth'
import { isPathMatch } from '@/utils/validate'
import { isRelogin } from '@/utils/request'

NProgress.configure({ showSpinner: false })

const whiteList = ['/login', '/register', '/ai/login', '/ai/register']
const aiProtectedPaths = ['/ai/chat', '/ai/chat/mobile']

const isWhiteList = (path) => {
  return whiteList.some(pattern => isPathMatch(pattern, path))
}

const isAiProtectedPath = (path) => {
  return aiProtectedPaths.some(pattern => isPathMatch(pattern, path))
}

// 检查AI登录状态
const checkAiLoginStatus = () => {
  const token = localStorage.getItem('token')
  const isLoggedIn = localStorage.getItem('isLoggedIn')
  return token && isLoggedIn === 'true'
}

router.beforeEach((to, from, next) => {
  NProgress.start()

  // 处理AI相关路径的特殊逻辑
  if (to.path.startsWith('/ai/')) {
    const isAiLoggedIn = checkAiLoginStatus()

    // 如果访问AI登录页面且已经登录，重定向到聊天页面
    if ((to.path === '/ai/login' || to.path === '/ai/register') && isAiLoggedIn) {
      console.log('==> 已登录，重定向到聊天页面')
      next('/ai/chat')
      NProgress.done()
      return
    }

    // 如果访问需要登录的AI页面但未登录，重定向到登录页面
    if (isAiProtectedPath(to.path) && !isAiLoggedIn) {
      console.log('==> 未登录，重定向到AI登录页面')
      next('/ai/login')
      NProgress.done()
      return
    }

    // AI路径且符合条件，直接放行
    next()
    NProgress.done()
    return
  }

  // 原有的后台系统路由守卫逻辑
  if (getToken()) {
    to.meta.title && store.dispatch('settings/setTitle', to.meta.title)
    /* has token*/
    if (to.path === '/login') {
      next({ path: '/' })
      NProgress.done()
    } else if (isWhiteList(to.path)) {
      next()
    } else {
      if (store.getters.roles.length === 0) {
        isRelogin.show = true
        // 判断当前用户是否已拉取完user_info信息
        store.dispatch('GetInfo').then(() => {
          isRelogin.show = false
          store.dispatch('GenerateRoutes').then(accessRoutes => {
            // 根据roles权限生成可访问的路由表
            router.addRoutes(accessRoutes) // 动态添加可访问路由表
            next({ ...to, replace: true }) // hack方法 确保addRoutes已完成
          })
        }).catch(err => {
            store.dispatch('LogOut').then(() => {
              Message.error(err)
              next({ path: '/' })
            })
          })
      } else {
        next()
      }
    }
  } else {
    // 没有token
    if (isWhiteList(to.path)) {
      // 在免登录白名单，直接进入
      next()
    } else {
      next(`/login?redirect=${encodeURIComponent(to.fullPath)}`) // 否则全部重定向到登录页
      NProgress.done()
    }
  }
})

router.afterEach(() => {
  NProgress.done()
})
