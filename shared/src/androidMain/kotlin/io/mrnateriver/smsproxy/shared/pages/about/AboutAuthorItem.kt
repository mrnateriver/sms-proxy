package io.mrnateriver.smsproxy.shared.pages.about

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.mrnateriver.smsproxy.shared.BuildConfig
import io.mrnateriver.smsproxy.shared.R

@Composable
fun AboutAuthorItem(modifier: Modifier = Modifier) {
    val uriHandler = LocalUriHandler.current
    val authorWebPageUrl = BuildConfig.AUTHOR_WEB_PAGE_URL

    AboutListItem(
        modifier = modifier,
        text = authorWebPageUrl,
        title = stringResource(R.string.about_page_entry_author_title),
        image = {
            Image(
                painter = painterResource(id = R.drawable.gh_avatar),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(48.dp),
            )
        },
        onClick = { uriHandler.openUri(authorWebPageUrl) },
    )
    HorizontalDivider()
}

@Preview
@Composable
private fun AboutAuthorItemPreview() {
    AboutAuthorItem()
}
