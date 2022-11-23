"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[241],{3905:(e,n,t)=>{t.d(n,{Zo:()=>u,kt:()=>d});var a=t(7294);function r(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function i(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function l(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?i(Object(t),!0).forEach((function(n){r(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):i(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function s(e,n){if(null==e)return{};var t,a,r=function(e,n){if(null==e)return{};var t,a,r={},i=Object.keys(e);for(a=0;a<i.length;a++)t=i[a],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var i=Object.getOwnPropertySymbols(e);for(a=0;a<i.length;a++)t=i[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var o=a.createContext({}),p=function(e){var n=a.useContext(o),t=n;return e&&(t="function"==typeof e?e(n):l(l({},n),e)),t},u=function(e){var n=p(e.components);return a.createElement(o.Provider,{value:n},e.children)},c={inlineCode:"code",wrapper:function(e){var n=e.children;return a.createElement(a.Fragment,{},n)}},m=a.forwardRef((function(e,n){var t=e.components,r=e.mdxType,i=e.originalType,o=e.parentName,u=s(e,["components","mdxType","originalType","parentName"]),m=p(t),d=r,f=m["".concat(o,".").concat(d)]||m[d]||c[d]||i;return t?a.createElement(f,l(l({ref:n},u),{},{components:t})):a.createElement(f,l({ref:n},u))}));function d(e,n){var t=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var i=t.length,l=new Array(i);l[0]=m;var s={};for(var o in n)hasOwnProperty.call(n,o)&&(s[o]=n[o]);s.originalType=e,s.mdxType="string"==typeof e?e:r,l[1]=s;for(var p=2;p<i;p++)l[p]=t[p];return a.createElement.apply(null,l)}return a.createElement.apply(null,t)}m.displayName="MDXCreateElement"},2045:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>o,contentTitle:()=>l,default:()=>c,frontMatter:()=>i,metadata:()=>s,toc:()=>p});var a=t(7462),r=(t(7294),t(3905));const i={id:"feel-list-expressions",title:"List expressions",description:"This document outlines list expressions and examples."},l=void 0,s={unversionedId:"reference/language-guide/feel-list-expressions",id:"reference/language-guide/feel-list-expressions",title:"List expressions",description:"This document outlines list expressions and examples.",source:"@site/docs/reference/language-guide/feel-list-expressions.md",sourceDirName:"reference/language-guide",slug:"/reference/language-guide/feel-list-expressions",permalink:"/feel-scala/docs/next/reference/language-guide/feel-list-expressions",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/docs/reference/language-guide/feel-list-expressions.md",tags:[],version:"current",frontMatter:{id:"feel-list-expressions",title:"List expressions",description:"This document outlines list expressions and examples."},sidebar:"Reference",previous:{title:"Numeric expressions",permalink:"/feel-scala/docs/next/reference/language-guide/feel-numeric-expressions"},next:{title:"Context expressions",permalink:"/feel-scala/docs/next/reference/language-guide/feel-context-expressions"}},o={},p=[{value:"Literal",id:"literal",level:3},{value:"Get element",id:"get-element",level:3},{value:"Filter",id:"filter",level:3},{value:"Some",id:"some",level:3},{value:"Every",id:"every",level:3}],u={toc:p};function c(e){let{components:n,...t}=e;return(0,r.kt)("wrapper",(0,a.Z)({},u,t,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("h3",{id:"literal"},"Literal"),(0,r.kt)("p",null,"Creates a new list of the given elements. The elements can be of any type."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"[1,2,3,4]\n")),(0,r.kt)("p",null,"A list value can embed other list values."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"[[1,2], [3,4], [5,6]]\n")),(0,r.kt)("h3",{id:"get-element"},"Get element"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"a[i]\n")),(0,r.kt)("p",null,"Accesses an element of the list ",(0,r.kt)("inlineCode",{parentName:"p"},"a")," at index ",(0,r.kt)("inlineCode",{parentName:"p"},"i"),". The index starts at ",(0,r.kt)("inlineCode",{parentName:"p"},"1"),"."),(0,r.kt)("p",null,"If the index is out of the range of the list, it returns ",(0,r.kt)("inlineCode",{parentName:"p"},"null"),"."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"[1,2,3,4][1]           \n// 1\n\n[1,2,3,4][2]\n// 2    \n\n[1,2,3,4][4]                                   \n// 4\n\n[1,2,3,4][5]\n// null\n    \n[1,2,3,4][0]                                   \n// null\n")),(0,r.kt)("p",null,"If the index is negative, it starts counting the elements from the end of the list. The last\nelement of the list is at index ",(0,r.kt)("inlineCode",{parentName:"p"},"-1"),"."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"[1,2,3,4][-1]                                  \n// 4\n\n[1,2,3,4][-2]                                  \n// 3\n\n[1,2,3,4][-5]                                   \n// null\n")),(0,r.kt)("admonition",{title:"be careful!",type:"caution"},(0,r.kt)("p",{parentName:"admonition"},"The index of a list starts at ",(0,r.kt)("inlineCode",{parentName:"p"},"1"),". In other languages, the index starts at ",(0,r.kt)("inlineCode",{parentName:"p"},"0"),".")),(0,r.kt)("h3",{id:"filter"},"Filter"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"a[c]\n")),(0,r.kt)("p",null,"Filters the list ",(0,r.kt)("inlineCode",{parentName:"p"},"a")," by the condition ",(0,r.kt)("inlineCode",{parentName:"p"},"c"),". The result of the expression is a list that contains all elements where the condition ",(0,r.kt)("inlineCode",{parentName:"p"},"c")," evaluates to ",(0,r.kt)("inlineCode",{parentName:"p"},"true"),"."),(0,r.kt)("p",null,"While filtering, the current element is assigned to the variable ",(0,r.kt)("inlineCode",{parentName:"p"},"item"),"."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"[1,2,3,4][item > 2]   \n// [3,4]\n\n[1,2,3,4][item > 10]\n// []\n\n[1,2,3,4][even(item)]\n// [2,4]\n")),(0,r.kt)("h3",{id:"some"},"Some"),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"some a in b satisfies c\n")),(0,r.kt)("p",null,"Iterates over the list ",(0,r.kt)("inlineCode",{parentName:"p"},"b")," and evaluate the condition ",(0,r.kt)("inlineCode",{parentName:"p"},"c")," for each element in the list. The current\nelement is assigned to the variable ",(0,r.kt)("inlineCode",{parentName:"p"},"a"),"."),(0,r.kt)("p",null,"It returns ",(0,r.kt)("inlineCode",{parentName:"p"},"true")," if ",(0,r.kt)("inlineCode",{parentName:"p"},"c")," evaluates to ",(0,r.kt)("inlineCode",{parentName:"p"},"true")," for ",(0,r.kt)("strong",{parentName:"p"},"one or more")," elements of ",(0,r.kt)("inlineCode",{parentName:"p"},"b"),". Otherwise, it\nreturns ",(0,r.kt)("inlineCode",{parentName:"p"},"false"),"."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"some x in [1,2,3] satisfies x > 2         \n// true\n\nsome x in [1,2,3] satisfies x > 5   \n// false\n\nsome x in [1,2,3] satisfies even(x)\n// true\n\nsome x in [1,2], y in [2,3] satisfies x < y  \n// true\n")),(0,r.kt)("h3",{id:"every"},"Every"),(0,r.kt)("p",null,"Iterates over the list ",(0,r.kt)("inlineCode",{parentName:"p"},"b")," and evaluate the condition ",(0,r.kt)("inlineCode",{parentName:"p"},"c")," for each element in the list. The current\nelement is assigned to the variable ",(0,r.kt)("inlineCode",{parentName:"p"},"a"),"."),(0,r.kt)("p",null,"It returns ",(0,r.kt)("inlineCode",{parentName:"p"},"true")," if ",(0,r.kt)("inlineCode",{parentName:"p"},"c")," evaluates to ",(0,r.kt)("inlineCode",{parentName:"p"},"true")," for ",(0,r.kt)("strong",{parentName:"p"},"all")," elements of ",(0,r.kt)("inlineCode",{parentName:"p"},"b"),". Otherwise, it\nreturns ",(0,r.kt)("inlineCode",{parentName:"p"},"false"),"."),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"every x in [1,2,3] satisfies x >= 1   \n// true\n\nevery x in [1,2,3] satisfies x >= 2     \n// false\n\nevery x in [1,2,3] satisfies even(x)\n// false\n\nevery x in [1,2], y in [2,3] satisfies x < y \n// false\n")))}c.isMDXComponent=!0}}]);