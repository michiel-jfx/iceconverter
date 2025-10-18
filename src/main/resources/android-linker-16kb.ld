SECTIONS
{
  . = ALIGN(16384);
  .text : ALIGN(16384) { *(.text .text.*) }
  . = ALIGN(16384);
  .rodata : ALIGN(16384) { *(.rodata .rodata.*) }
  . = ALIGN(16384);
  .data : ALIGN(16384) { *(.data .data.*) }
  . = ALIGN(16384);
  .data.rel.ro : ALIGN(16384) { *(.data.rel.ro .data.rel.ro.*) }
  . = ALIGN(16384);
  .bss : ALIGN(16384) { *(.bss .bss.*) }
}
INSERT AFTER .text;
