int g; /* +1 symbol */

struct s { /* +1 symbol and scope */
  int a; /* +1 symbol */
}

void myFunction( /* +1 symbol and scope */
  int p /* +1 symbol */
) { /* +1 scope */
  int g = anotherFunction(); /* +1 symbol */
  { /* +1 scope */
    int local; /* +1 symbol */
    g = 0;
    local = 1;
  }
  p = 1;
}

int anotherFunction( /* +1 symbol and scope */
) { /* +1 scope */
}
