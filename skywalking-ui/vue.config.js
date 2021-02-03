/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

var webpack = require('webpack');

module.exports = {
    configureWebpack: {
        plugins: [
            new webpack.ProvidePlugin({
                $: 'jquery',
                jQuery: 'jquery',
                'windows.jQuery': 'jquery',
                Popper: ['popper.js', 'default'],
            }),
        ],
    },
    publicPath: process.env.NODE_ENV === 'production' ? '/' : '/',
    lintOnSave: false,
    devServer: {
        proxy: {
            '/graphql': {
                target: `${process.env.SW_PROXY_TARGET || 'http://127.0.0.1:12801'}`,
                changeOrigin: true,
            },
            '/monitor': {
                target: `${process.env.SW_PROXY_TARGET || 'http://1.119.169.74:10002/monitor'}`,//http://10.1.100.35:12801
                changeOrigin: true,
                pathRewrite: {
                    ["^/monitor"]: ""
                }
            },
        },
    },
    chainWebpack: (config) => {
        /* const svgRule = config.module.rule('svg');
                                                                svgRule.uses.clear();
                                                                svgRule
                                                                    .use('svg-sprite-loader')
                                                                    .loader('svg-sprite-loader')
                                                                    .options({
                                                                        symbolId: '[name]',
                                                                    }); */
        /*  const CssRule = config.module.rule('css');
                                                                 CssRule.uses.clear();
                                                                 CssRule.loader('css-loader')
                                                                 CssRule.loader('style-loader') */
        config.module
            .rule('css')
            .test(/\.css$/)
            .oneOf('vue')
            .resourceQuery(/\?vue/)
            .use('px2rem')
            .loader('px2rem-loader')
            .options({
                remUnit: 192,
            });
    },
    css: {
        loaderOptions: {
            sass: {
                data: `@import "./src/assets/css/global.scss";`,
            },
        },
    },
};
