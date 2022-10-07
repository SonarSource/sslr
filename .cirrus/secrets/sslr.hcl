secret {
    no_prefix = true
    path      = "github/token/SonarSource-sslr-ro"
    key {
        name   = "token"
        format = "GITHUB_TOKEN"
    }
}
secret {
    no_prefix = true
    path      = "kv/data/repox"
    key {
        name   = "url"
        format = "ARTIFACTORY_URL"
    }
}
secret {
    no_prefix = true
    path      = "artifactory/token/SonarSource-sslr-private-reader"
    key {
        name   = "access_token"
        format = "ARTIFACTORY_PRIVATE_PASSWORD"
    }
    key {
        name   = "access_token"
        format = "ARTIFACTORY_API_KEY"
    }
}
secret {
    no_prefix = true
    path      = "artifactory/token/SonarSource-sslr-qa-deployer"
    key {
        name   = "access_token"
        format = "ARTIFACTORY_DEPLOY_PASSWORD"
    }
}
secret {
    no_prefix = true
    path      = "kv/data/burgr"
    key {
        name   = "url"
        format = "BURGR_URL"
    }
    key {
        name   = "cirrus_username"
        format = "BURGR_USERNAME"
    }
    key {
        name   = "cirrus_password"
        format = "BURGR_PASSWORD"
    }
}
secret {
    no_prefix = true
    path      = "kv/data/next"
    key {
        name   = "token"
        format = "SONAR_TOKEN"
    }
}
secret {
    no_prefix = true
    path      = "kv/data/sign"
    key {
        name   = "passphrase"
        format = "PGP_PASSPHRASE"
    }
}
secret {
    no_prefix = true
    path      = "kv/data/promote"
    key {
        name   = "token"
        format = "GCF_ACCESS_TOKEN"
    }
    key {
        name   = "url"
        format = "PROMOTE_URL"
    }
}
