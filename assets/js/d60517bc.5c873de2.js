"use strict";(self.webpackChunkfeel_scala=self.webpackChunkfeel_scala||[]).push([[6509],{3905:(e,n,t)=>{t.d(n,{Zo:()=>c,kt:()=>d});var a=t(7294);function r(e,n,t){return n in e?Object.defineProperty(e,n,{value:t,enumerable:!0,configurable:!0,writable:!0}):e[n]=t,e}function l(e,n){var t=Object.keys(e);if(Object.getOwnPropertySymbols){var a=Object.getOwnPropertySymbols(e);n&&(a=a.filter((function(n){return Object.getOwnPropertyDescriptor(e,n).enumerable}))),t.push.apply(t,a)}return t}function i(e){for(var n=1;n<arguments.length;n++){var t=null!=arguments[n]?arguments[n]:{};n%2?l(Object(t),!0).forEach((function(n){r(e,n,t[n])})):Object.getOwnPropertyDescriptors?Object.defineProperties(e,Object.getOwnPropertyDescriptors(t)):l(Object(t)).forEach((function(n){Object.defineProperty(e,n,Object.getOwnPropertyDescriptor(t,n))}))}return e}function o(e,n){if(null==e)return{};var t,a,r=function(e,n){if(null==e)return{};var t,a,r={},l=Object.keys(e);for(a=0;a<l.length;a++)t=l[a],n.indexOf(t)>=0||(r[t]=e[t]);return r}(e,n);if(Object.getOwnPropertySymbols){var l=Object.getOwnPropertySymbols(e);for(a=0;a<l.length;a++)t=l[a],n.indexOf(t)>=0||Object.prototype.propertyIsEnumerable.call(e,t)&&(r[t]=e[t])}return r}var s=a.createContext({}),u=function(e){var n=a.useContext(s),t=n;return e&&(t="function"==typeof e?e(n):i(i({},n),e)),t},c=function(e){var n=u(e.components);return a.createElement(s.Provider,{value:n},e.children)},p={inlineCode:"code",wrapper:function(e){var n=e.children;return a.createElement(a.Fragment,{},n)}},f=a.forwardRef((function(e,n){var t=e.components,r=e.mdxType,l=e.originalType,s=e.parentName,c=o(e,["components","mdxType","originalType","parentName"]),f=u(t),d=r,m=f["".concat(s,".").concat(d)]||f[d]||p[d]||l;return t?a.createElement(m,i(i({ref:n},c),{},{components:t})):a.createElement(m,i({ref:n},c))}));function d(e,n){var t=arguments,r=n&&n.mdxType;if("string"==typeof e||r){var l=t.length,i=new Array(l);i[0]=f;var o={};for(var s in n)hasOwnProperty.call(n,s)&&(o[s]=n[s]);o.originalType=e,o.mdxType="string"==typeof e?e:r,i[1]=o;for(var u=2;u<l;u++)i[u]=t[u];return a.createElement.apply(null,i)}return a.createElement.apply(null,t)}f.displayName="MDXCreateElement"},3242:(e,n,t)=>{t.d(n,{Z:()=>r});var a=t(7294);const r=()=>a.createElement("p",null,a.createElement("span",{style:{backgroundColor:"#FC5D0D",borderRadius:"7px",color:"#fff",padding:"0.2rem",marginRight:"0.5rem"},title:"This feature is not part of the official DMN standard. It is an extension from Camunda's implementation."},"Camunda Extension"))},9823:(e,n,t)=>{t.r(n),t.d(n,{assets:()=>u,contentTitle:()=>o,default:()=>f,frontMatter:()=>i,metadata:()=>s,toc:()=>c});var a=t(7462),r=(t(7294),t(3905)),l=t(3242);const i={id:"feel-built-in-functions-boolean",title:"Boolean functions",description:"This document outlines current boolean functions and a few examples."},o=void 0,s={unversionedId:"reference/builtin-functions/feel-built-in-functions-boolean",id:"reference/builtin-functions/feel-built-in-functions-boolean",title:"Boolean functions",description:"This document outlines current boolean functions and a few examples.",source:"@site/docs/reference/builtin-functions/feel-built-in-functions-boolean.md",sourceDirName:"reference/builtin-functions",slug:"/reference/builtin-functions/feel-built-in-functions-boolean",permalink:"/feel-scala/docs/next/reference/builtin-functions/feel-built-in-functions-boolean",draft:!1,editUrl:"https://github.com/camunda/feel-scala/edit/main/docs/docs/reference/builtin-functions/feel-built-in-functions-boolean.md",tags:[],version:"current",frontMatter:{id:"feel-built-in-functions-boolean",title:"Boolean functions",description:"This document outlines current boolean functions and a few examples."},sidebar:"Reference",previous:{title:"Conversion functions",permalink:"/feel-scala/docs/next/reference/builtin-functions/feel-built-in-functions-conversion"},next:{title:"String functions",permalink:"/feel-scala/docs/next/reference/builtin-functions/feel-built-in-functions-string"}},u={},c=[{value:"not(negand)",id:"notnegand",level:2},{value:"is defined(value)",id:"is-definedvalue",level:2},{value:"get or else(value, default)",id:"get-or-elsevalue-default",level:2},{value:"assert(value, condition)",id:"assertvalue-condition",level:2},{value:"assert(value, condition, cause)",id:"assertvalue-condition-cause",level:2}],p={toc:c};function f(e){let{components:n,...t}=e;return(0,r.kt)("wrapper",(0,a.Z)({},p,t,{components:n,mdxType:"MDXLayout"}),(0,r.kt)("h2",{id:"notnegand"},"not(negand)"),(0,r.kt)("p",null,"Returns the logical negation of the given value."),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Function signature")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"not(negand: boolean): boolean\n")),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Examples")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"not(true)\n// false\n\nnot(null) \n// null\n")),(0,r.kt)("h2",{id:"is-definedvalue"},"is defined(value)"),(0,r.kt)(l.Z,{mdxType:"MarkerCamundaExtension"}),(0,r.kt)("p",null,"Checks if a given value is defined. A value is defined if it exists, and it is an instance of one of the FEEL data types including ",(0,r.kt)("inlineCode",{parentName:"p"},"null"),"."),(0,r.kt)("p",null,"The function can be used to check if a variable or a context entry (e.g. a property of a variable) exists. It allows differentiating between a ",(0,r.kt)("inlineCode",{parentName:"p"},"null")," variable and a value that doesn't exist."),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Function signature")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"is defined(value: Any): boolean\n")),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Examples")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},'is defined(1)\n// true\n\nis defined(null)\n// true\n\nis defined(x)\n// false - if no variable "x" exists\n\nis defined(x.y)\n// false - if no variable "x" exists or it doesn\'t have a property "y"\n')),(0,r.kt)("h2",{id:"get-or-elsevalue-default"},"get or else(value, default)"),(0,r.kt)(l.Z,{mdxType:"MarkerCamundaExtension"}),(0,r.kt)("p",null,"Return the provided value parameter if not ",(0,r.kt)("inlineCode",{parentName:"p"},"null"),", otherwise return the default parameter"),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Function signature")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"get or else(value: Any, default: Any): Any\n")),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Examples")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},'get or default("this", "default")\n// "this"\n\nget or default(null, "default")\n// "default"\n\nget or default(null, null)     \n// null\n')),(0,r.kt)("h2",{id:"assertvalue-condition"},"assert(value, condition)"),(0,r.kt)(l.Z,{mdxType:"MarkerCamundaExtension"}),(0,r.kt)("p",null,"Verify that the given condition is met. If the condition is ",(0,r.kt)("inlineCode",{parentName:"p"},"true"),", the function returns the value.\nOtherwise, the evaluation fails with an error."),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Function signature")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"assert(value: Any, condition: Any)\n")),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Examples")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},'assert(x, x != null)\n// "value" - if x is "value"\n// error - if x is null or doesn\'t exist\n\nassert(x, x >= 0) \n// 4 - if x is 4\n// error - if x is less than zero\n')),(0,r.kt)("h2",{id:"assertvalue-condition-cause"},"assert(value, condition, cause)"),(0,r.kt)(l.Z,{mdxType:"MarkerCamundaExtension"}),(0,r.kt)("p",null,"Verify that the given condition is met. If the condition is ",(0,r.kt)("inlineCode",{parentName:"p"},"true"),", the function returns the value.\nOtherwise, the evaluation fails with an error containing the given message."),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Function signature")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"assert(value: Any, condition: Any, cause: String)\n")),(0,r.kt)("p",null,(0,r.kt)("strong",{parentName:"p"},"Examples")),(0,r.kt)("pre",null,(0,r.kt)("code",{parentName:"pre",className:"language-js"},"assert(x, x != null, \"'x' should not be null\")\n// \"value\" - if x is \"value\"\n// error('x' should not be null) - if x is null or doesn't exist\n\nassert(x, x >= 0, \"'x' should be positive\")\n// 4 - if x is 4\n// error('x' should be positive) - if x is less than zero\n")))}f.isMDXComponent=!0}}]);