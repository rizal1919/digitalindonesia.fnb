package com.digitalindonesia.fnb.fragments;

import android.app.AlertDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.digitalindonesia.fnb.R;
import com.digitalindonesia.fnb.cart.CartManager;
import com.digitalindonesia.fnb.model.MenuItem;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class SplitBillPerItemFragment extends DialogFragment {

    class Person {
        String id;
        String name;
        boolean isCustomName = false;
        double totalTagihan = 0;
        int totalBarang = 0;

        // Atribut Pembayaran
        boolean isPaid = false;
        String paymentMethod = "Cash";
        double amountPaid = 0;
        double change = 0;

        Person() { this.id = String.valueOf(System.currentTimeMillis()); }
    }

    class SplitItem {
        MenuItem menu;
        int maxQty, sisaQty;
        HashMap<String, Integer> alokasiOrang = new HashMap<>();

        SplitItem(MenuItem menu) {
            this.menu = menu;
            this.maxQty = menu.getQuantity();
            this.sisaQty = menu.getQuantity();
        }

        int getAlokasi(String personId) { return alokasiOrang.getOrDefault(personId, 0); }

        void tambahAlokasi(String personId) {
            if (sisaQty > 0) {
                sisaQty--;
                alokasiOrang.put(personId, getAlokasi(personId) + 1);
            }
        }

        void kurangiAlokasi(String personId) {
            if (getAlokasi(personId) > 0) {
                sisaQty++;
                alokasiOrang.put(personId, getAlokasi(personId) - 1);
            }
        }
    }

    private List<Person> peopleList = new ArrayList<>();
    private List<SplitItem> splitItemsList = new ArrayList<>();
    private Person selectedPerson = null;

    private RecyclerView rvPeopleChips, rvSplitItems, rvPaymentCards;
    private TextView tvGlobalSummary, tvTargetPersonName, tvTargetPersonTotal;
    private MaterialButton btnKonfirmasiBayar;
    private PeopleAdapter peopleAdapter;
    private ItemAdapter itemAdapter;
    private PaymentAdapter paymentAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NORMAL, android.R.style.Theme_Light_NoTitleBar_Fullscreen);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_split_bill_per_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.btnBatalkanSplit).setOnClickListener(v -> dismiss());
        tvGlobalSummary = view.findViewById(R.id.tvGlobalSummary);
        tvTargetPersonName = view.findViewById(R.id.tvTargetPersonName);
        tvTargetPersonTotal = view.findViewById(R.id.tvTargetPersonTotal);
        btnKonfirmasiBayar = view.findViewById(R.id.btnKonfirmasiBayar);

        rvPeopleChips = view.findViewById(R.id.rvPeopleChips);
        rvSplitItems = view.findViewById(R.id.rvSplitItems);
        rvPaymentCards = view.findViewById(R.id.rvPaymentCards);

        // Tarik data dari Keranjang
        int totalGlobalItems = 0;
        for (MenuItem item : CartManager.getInstance().getCartItems()) {
            splitItemsList.add(new SplitItem(item));
            totalGlobalItems += item.getQuantity();
        }

        // --- BUG FIX 1: By default hanya ada 1 orang ---
        Person firstPerson = new Person();
        peopleList.add(firstPerson);
        selectedPerson = firstPerson;
        renamePeople(); // Berikan nama otomatis

        tvGlobalSummary.setText(peopleList.size() + " Payer • " + totalGlobalItems + " Item");

        rvPeopleChips.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        peopleAdapter = new PeopleAdapter();
        rvPeopleChips.setAdapter(peopleAdapter);

        rvSplitItems.setLayoutManager(new LinearLayoutManager(getContext()));
        itemAdapter = new ItemAdapter();
        rvSplitItems.setAdapter(itemAdapter);

        rvPaymentCards.setLayoutManager(new LinearLayoutManager(getContext()));
        paymentAdapter = new PaymentAdapter();
        rvPaymentCards.setAdapter(paymentAdapter);

        view.findViewById(R.id.btnAddPerson).setOnClickListener(v -> {
            Person newPerson = new Person();
            peopleList.add(newPerson);
            selectedPerson = newPerson;
            renamePeople();
            refreshAllUI();
        });

        btnKonfirmasiBayar.setOnClickListener(v -> {
            CartManager.getInstance().clear();
            Toast.makeText(getContext(), "Transaksi Berhasil!", Toast.LENGTH_LONG).show();
            requireActivity().finish();
        });

        refreshAllUI();
    }

    // --- BUG FIX 2: Urutkan nama jika ada yang dihapus (Kecuali yang dicustom) ---
    private void renamePeople() {
        int counter = 1;
        for (Person p : peopleList) {
            if (!p.isCustomName) {
                p.name = "Orang " + counter;
            }
            counter++;
        }
    }

    private void refreshAllUI() {
        boolean isAllPaid = true;
        boolean hasAllocatedItems = false;

        for (Person p : peopleList) {
            p.totalTagihan = 0;
            p.totalBarang = 0;
            for (SplitItem sItem : splitItemsList) {
                int qtyAllocated = sItem.getAlokasi(p.id);
                p.totalBarang += qtyAllocated;
                p.totalTagihan += (qtyAllocated * sItem.menu.getFinalPricePerUnit());
            }

            // Validasi tombol Konfirmasi Selesai
            if (p.totalTagihan > 0) {
                hasAllocatedItems = true;
                if (!p.isPaid) isAllPaid = false;
            }
        }

        // Aktifkan tombol konfirmasi jika ada barang dan semua yang menanggung sdh bayar
        if (hasAllocatedItems && isAllPaid) {
            btnKonfirmasiBayar.setEnabled(true);
            btnKonfirmasiBayar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#00796B")));
        } else {
            btnKonfirmasiBayar.setEnabled(false);
            btnKonfirmasiBayar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#BDBDBD")));
        }

        if (selectedPerson != null) {
            tvTargetPersonName.setText(selectedPerson.name);
            tvTargetPersonTotal.setText(formatRp(selectedPerson.totalTagihan));
        }

        int globalSisa = 0;
        for (SplitItem sItem : splitItemsList) globalSisa += sItem.sisaQty;
        tvGlobalSummary.setText(peopleList.size() + " Payer • Sisa " + globalSisa + " Item Belum Dibagi");

        peopleAdapter.notifyDataSetChanged();
        itemAdapter.notifyDataSetChanged();
        paymentAdapter.notifyDataSetChanged();
    }

    private String formatRp(double amount) {
        NumberFormat format = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
        format.setMaximumFractionDigits(0);
        return format.format(amount).replace("Rp", "Rp ");
    }

    // ================= ADAPTERS =================

    class PeopleAdapter extends RecyclerView.Adapter<PeopleAdapter.VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_split_chip, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            Person p = peopleList.get(position);
            holder.chip.setText(p.name);

            boolean isSelected = (selectedPerson != null && selectedPerson.id.equals(p.id));
            if (isSelected) {
                holder.chip.setChipBackgroundColor(ColorStateList.valueOf(Color.parseColor("#E0F2F1")));
                holder.chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#00796B")));
                holder.chip.setTextColor(Color.parseColor("#00796B"));
                holder.chip.setChipIconResource(R.drawable.ic_check);
                holder.chip.setChipIconTint(ColorStateList.valueOf(Color.parseColor("#00796B")));
            } else {
                holder.chip.setChipBackgroundColor(ColorStateList.valueOf(Color.WHITE));
                holder.chip.setChipStrokeColor(ColorStateList.valueOf(Color.parseColor("#E0E0E0")));
                holder.chip.setTextColor(Color.BLACK);
                holder.chip.setChipIcon(null);
            }

            holder.chip.setOnClickListener(v -> {
                selectedPerson = p;
                refreshAllUI();
            });

            holder.chip.setOnCloseIconClickListener(v -> {
                if (peopleList.size() <= 1) return;
                for (SplitItem sItem : splitItemsList) {
                    sItem.sisaQty += sItem.getAlokasi(p.id);
                    sItem.alokasiOrang.remove(p.id);
                }
                peopleList.remove(p);
                renamePeople(); // <-- Rapikan urutan angka
                if (selectedPerson.id.equals(p.id)) selectedPerson = peopleList.get(0);
                refreshAllUI();
            });
        }
        @Override public int getItemCount() { return peopleList.size(); }
        class VH extends RecyclerView.ViewHolder { Chip chip; VH(View v) { super(v); chip = v.findViewById(R.id.chipPerson); } }
    }

    class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_split_menu_stepper, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            SplitItem sItem = splitItemsList.get(position);
            holder.tvName.setText(sItem.menu.getName());

            if (sItem.sisaQty == 0) {
                holder.tvSisa.setText("Sisa Item: Habis");
                holder.tvSisa.setTextColor(Color.GRAY);
                holder.tvSisa.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#EEEEEE")));
            } else {
                holder.tvSisa.setText("Sisa Item: " + sItem.sisaQty + "x");
                holder.tvSisa.setTextColor(Color.parseColor("#2E7D32"));
                holder.tvSisa.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E8F5E9")));
            }

            int qtyForTarget = (selectedPerson != null) ? sItem.getAlokasi(selectedPerson.id) : 0;
            holder.tvQty.setText(String.valueOf(qtyForTarget));

            holder.btnPlus.setOnClickListener(v -> {
                if (selectedPerson != null && sItem.sisaQty > 0) {
                    sItem.tambahAlokasi(selectedPerson.id);
                    refreshAllUI();
                } else if (sItem.sisaQty == 0) {
                    Toast.makeText(getContext(), "Barang sudah habis dialokasikan", Toast.LENGTH_SHORT).show();
                }
            });

            holder.btnMinus.setOnClickListener(v -> {
                if (selectedPerson != null && qtyForTarget > 0) {
                    sItem.kurangiAlokasi(selectedPerson.id);
                    refreshAllUI();
                }
            });
        }
        @Override public int getItemCount() { return splitItemsList.size(); }
        class VH extends RecyclerView.ViewHolder { TextView tvName, tvSisa, tvQty; View btnPlus, btnMinus; VH(View v) { super(v); tvName = v.findViewById(R.id.tvMenuName); tvSisa = v.findViewById(R.id.tvSisaItem); tvQty = v.findViewById(R.id.tvQtyAlokasi); btnPlus = v.findViewById(R.id.btnPlus); btnMinus = v.findViewById(R.id.btnMinus); } }
    }

    class PaymentAdapter extends RecyclerView.Adapter<PaymentAdapter.VH> {
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VH(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_split_payment_card, parent, false));
        }

        @Override public void onBindViewHolder(@NonNull VH holder, int position) {
            Person p = peopleList.get(position);

            // Atur Isi Data Umum
            holder.tvName.setText(p.name);
            holder.tvTotal.setText(formatRp(p.totalTagihan));
            holder.tvItemsCount.setText(p.totalBarang + " Item ⓘ");

            // --- Fitur Edit Nama ---
            holder.icEdit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Ubah Nama Payer");
                final EditText input = new EditText(getContext());
                input.setText(p.name);
                builder.setView(input);
                builder.setPositiveButton("Simpan", (dialog, which) -> {
                    String newName = input.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        p.name = newName;
                        p.isCustomName = true; // Tandai agar tidak di-rename otomatis
                        refreshAllUI();
                    }
                });
                builder.setNegativeButton("Batal", (dialog, which) -> dialog.cancel());
                builder.show();
            });

            // WUJUD CARD BISA BERUBAH TERGANTUNG STATUS BAYAR
            if (p.isPaid) {
                // 1. Tampilkan Wujud Status Sukses
                holder.llUnpaidView.setVisibility(View.GONE);
                holder.llPaidView.setVisibility(View.VISIBLE);

                holder.tvPaidName.setText(p.name);
                holder.tvPaidTotal.setText(formatRp(p.totalTagihan));
                holder.tvPaidItemsCount.setText(p.totalBarang + " Item ⓘ");
                holder.tvPaidDesc.setText(p.paymentMethod + " · Kembalian: " + formatRp(p.change));

                // Batal Bayar
                holder.btnUbahStatus.setOnClickListener(v -> {
                    p.isPaid = false;
                    refreshAllUI();
                });

            } else {
                // 2. Tampilkan Wujud Form Belum Bayar
                holder.llUnpaidView.setVisibility(View.VISIBLE);
                holder.llPaidView.setVisibility(View.GONE);

                // Spinner Metode
                // Menampilkan nama metode default
                holder.tvSelectedMethod.setText(p.paymentMethod);

                // Popup Menu Custom yang Rapi
                holder.btnSelectMethod.setOnClickListener(v -> {
                    android.widget.PopupMenu popup = new android.widget.PopupMenu(getContext(), holder.btnSelectMethod);
                    popup.getMenu().add("Cash");
                    popup.getMenu().add("Transfer");
                    popup.getMenu().add("QRIS");

                    popup.setOnMenuItemClickListener(item -> {
                        p.paymentMethod = item.getTitle().toString();
                        holder.tvSelectedMethod.setText(p.paymentMethod);
                        return true;
                    });
                    popup.show();
                });

                // Cek isi EditText agar mengikuti harga tagihan jika kosong
                if (holder.etPay.getText().toString().isEmpty()) {
                    holder.etPay.setText(String.format(Locale.US, "%.0f", p.totalTagihan));
                }

                // Proses Bayar
                holder.btnPay.setOnClickListener(v -> {
                    if (p.totalTagihan == 0) {
                        Toast.makeText(getContext(), "Belum ada tagihan!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    String input = holder.etPay.getText().toString();
                    if (input.isEmpty()) input = "0";
                    double payAmount = Double.parseDouble(input);

                    if (payAmount < p.totalTagihan) {
                        Toast.makeText(getContext(), "Uang yang dibayar kurang!", Toast.LENGTH_SHORT).show();
                    }  else {
                        // Selesaikan Transaksi untuk Orang ini
                        p.paymentMethod = holder.tvSelectedMethod.getText().toString(); // <--- GANTI JADI INI
                        p.amountPaid = payAmount;
                        p.change = payAmount - p.totalTagihan;
                        p.isPaid = true;
                        refreshAllUI();
                    }
                });
            }
        }
        @Override public int getItemCount() { return peopleList.size(); }
        class VH extends RecyclerView.ViewHolder {
            View llUnpaidView, llPaidView, icEdit;
            TextView tvName, tvTotal, tvItemsCount, tvPaidName, tvPaidDesc, tvPaidTotal, tvPaidItemsCount, btnUbahStatus;
            EditText etPay; View btnPay;
            View btnSelectMethod;
            TextView tvSelectedMethod;

            VH(View v) {
                super(v);
                llUnpaidView = v.findViewById(R.id.llUnpaidView);
                llPaidView = v.findViewById(R.id.llPaidView);
                icEdit = v.findViewById(R.id.icEdit);

                tvName = v.findViewById(R.id.tvPersonName);
                tvTotal = v.findViewById(R.id.tvPersonTotal);
                tvItemsCount = v.findViewById(R.id.tvPersonItemsCount);
                etPay = v.findViewById(R.id.etPayAmount);
                btnSelectMethod = v.findViewById(R.id.btnSelectMethod);
                tvSelectedMethod = v.findViewById(R.id.tvSelectedMethod);
                btnPay = v.findViewById(R.id.btnPayAction);

                tvPaidName = v.findViewById(R.id.tvPaidName);
                tvPaidDesc = v.findViewById(R.id.tvPaidDesc);
                tvPaidTotal = v.findViewById(R.id.tvPaidTotal);
                tvPaidItemsCount = v.findViewById(R.id.tvPaidItemsCount);
                btnUbahStatus = v.findViewById(R.id.btnUbahStatus);
            }
        }
    }
}