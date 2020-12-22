import { constantRoutes } from '@/router-dynamic'
import hongtuConfig from '@/utils/services';
import Home from '@/page/home.vue'

const permission = {
    state: {
        routes: [],
        addRoutes: []
    },
    mutations: {
        SET_ROUTES: (state: any, routes: any) => {
            state.addRoutes = routes
            state.routes = constantRoutes.concat(routes)
        }
    },
    actions: {
        // 生成路由
        GenerateRoutes(commit:any) {
            return new Promise(resolve => {
                // 向后端请求路由数据
                hongtuConfig.getRouters().then((res: any) => {
                    const accessedRoutes = filterAsyncRouter(res.data);
                    commit('SET_ROUTES', accessedRoutes);
                    resolve(accessedRoutes);
                })
            })
        }
    }
}

// 遍历后台传来的路由字符串，转换为组件对象
function filterAsyncRouter(asyncRouterMap: any) {
    return asyncRouterMap.filter((route: any) => {
        if (route.component) {
            if(route.component == 'Layout') {
                route.component = Home
            } else {
                route.component = loadView(route.component)
            }
        }
        if (route.children != null && route.children && route.children.length) {
            route.children = filterAsyncRouter(route.children)
        }
        return true
    })
}

export const loadView = (view: any) => { // 路由懒加载
    return () => import(`@/page/${view}`)
}

export default permission