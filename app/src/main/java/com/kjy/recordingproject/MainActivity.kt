package com.kjy.recordingproject

import android.annotation.SuppressLint
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ProgressBar
import androidx.core.widget.ContentLoadingProgressBar
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

class MainActivity : AppCompatActivity() {

    // 웹뷰 변수 선언
    private val webView: WebView by lazy {
        findViewById(R.id.webView)
    }

    // 프로그래스바 변수 선언
    private val progressBar: ContentLoadingProgressBar by lazy {
        findViewById(R.id.progressBar)
    }

    // swipeRefreshLayout 변수 선언
    private val refreshLayout: SwipeRefreshLayout by lazy {
        findViewById(R.id.swipeRefreshLayout)
    }

    // editText 변수 선언
    private val addressBar: EditText by lazy {
        findViewById(R.id.addressBar)
    }

    // 네비게이션 구현을 위한 버튼들 선언
    private val goHomeButton: ImageButton by lazy {
        findViewById(R.id.goHomeButton)
    }

    private val goBackButton: ImageButton by lazy {
        findViewById(R.id.goBackButton)
    }

    private val goForwardButton: ImageButton by lazy {
        findViewById(R.id.goForwardButton)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initViews()

        bindViews()
    }

    // 스마트폰자체의 뒤로가기 버튼 구현
    override fun onBackPressed() {

        // 뒤로 갈 수 있다면
        if (webView.canGoBack()) {
            webView.goBack()
        }else {
        super.onBackPressed()
        }
    }

    // 뷰들을 초기화 하는 함수
    // 명시된 url을 불러오는 로직
    @SuppressLint("SetJavaScriptEnabled")
    private fun initViews() {
        // 아래 두가지 옵션은 웹뷰에서 필수적
        // apply함수로 WebView를 this로 전달.
        webView.apply {
            // 지정한 웹뷰 영역으로 해줘야 기본 웹브라우저로 이동되지않음
            webViewClient = WebViewClient()
            // 얼마나 로딩됐는지 파악하기 위해 webChromeClient가 필요함.
            webChromeClient = WebChromeClient()
            // 안드로이드에서는 javascript를 기본적으로 허용하지 않고 있기 때문에 허용해주는 코드 작성
            settings.javaScriptEnabled = true
            loadUrl("http://www.google.com")
        }

    }

    private fun bindViews() {
        // 홈 버튼 구현
        goHomeButton.setOnClickListener {
            webView.loadUrl(DEFAULT_URL)
        }

        // 파라미터 3개, 액션이 발생한 뷰(주소창), 어떤 id에서 액션이 발생했는지, 이벤트
        addressBar.setOnEditorActionListener { v, actionId,event ->
            // 주소창에서 입력을 하고 done버튼을 눌렀을 때
            if(actionId == EditorInfo.IME_ACTION_DONE) {
                // editText에 입력한것을 toString()으로 변환하여 url을 불러옴.
                webView.loadUrl(v.text.toString())
            }

            return@setOnEditorActionListener false
        }

        // 웹뷰 뒤로가기 구현
        goBackButton.setOnClickListener {
            webView.goBack()
        }

        // 웹뷰 앞으로 가기 구현
        goForwardButton.setOnClickListener {
            webView.goForward()
        }

        // 스와이프 기능
        refreshLayout.setOnRefreshListener {
            webView.reload()
        }
    }

    // 객체로서 사용이 가능
    companion object {
        private const val DEFAULT_URL = "http://www.google.com"
    }

    /*
    swipeRefreshLayout의 로딩바를 지우기 위해 직접 웹뷰 클라이언트를 상속 받아서 웹뷰의 load가 끝났을 때 isRefreshing을
    false로 변경해준다.
     */
    inner class WebViewClient: android.webkit.WebViewClient() {
        // 프로그래스바가 로딩이 시작될때와 로딩이 끝나기 전까지만 보여줘야함.
        override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(view, url, favicon)

            progressBar.show()
        }

        // 웹뷰의 load가 끝났을 때 swipeRefreshLayout의 isRefreshing을 false로 변경해준다.

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)

            refreshLayout.isRefreshing = false
            progressBar.hide()
            // history가 없을때 backButton 비활성화 하기
            goBackButton.isEnabled = webView.canGoBack()
            goForwardButton.isEnabled = webView.canGoForward()
        }
    }

    // progressBar의 progress를 로딩상태에 따라 보여줌.
    inner class WebChromeClient: android.webkit.WebChromeClient() {
        override fun onProgressChanged(view: WebView?, newProgress: Int) {
            super.onProgressChanged(view, newProgress)

            progressBar.progress = newProgress
        }
    }
}