import React from 'react';
import { Card, CardContent } from '@/components/ui/card';

interface LowConfidenceStateProps {
    title: string;
    message: string;
}

export const LowConfidenceState: React.FC<LowConfidenceStateProps> = ({ title, message }) => {
    return (
        <Card className="border-dashed border-zinc-200 bg-zinc-50/70 dark:bg-zinc-900/40 backdrop-blur-sm">
            <CardContent className="p-4">
                <div className="text-[11px] font-semibold uppercase tracking-[0.25em] text-zinc-400">Low Confidence</div>
                <h3 className="mt-2 text-sm font-semibold text-zinc-700 dark:text-zinc-200">{title}</h3>
                <p className="mt-2 text-sm text-zinc-500 leading-relaxed">{message}</p>
            </CardContent>
        </Card>
    );
};